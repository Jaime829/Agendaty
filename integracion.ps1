$Base = "http://localhost:8080/api/v1"
$ErrorActionPreference = "Continue"

function Invoke($method, $path, $body, $auth) {
    $params = @{
        Uri = "$Base$path"; Method = $method; TimeoutSec = 15; ErrorAction = "Stop"
    }
    if ($body) { $params.ContentType = "application/json"; $params.Body = ($body | ConvertTo-Json -Depth 5) }
    if ($auth) { $params.Headers = @{ Authorization = "Bearer $auth"; "X-Usuario-Id" = $authUserId } }
    try {
        $resp = Invoke-WebRequest @params
        return @{ status = $resp.StatusCode; content = $resp.Content; obj = ($resp.Content | ConvertFrom-Json -Depth 5) }
    } catch {
        $rd = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($rd)
        $cuerpo = $reader.ReadToEnd()
        return @{ status = $_.Exception.Response.StatusCode.Value__; content = $cuerpo; obj = $null }
    }
}

$ok = 0; $fail = 0
function Check($nombre, $resultado, $esperado) {
    if ($resultado.status -eq $esperado) { Write-Host "  [OK] $nombre (HTTP $($resultado.status))" -ForegroundColor Green; $script:ok++ }
    else { Write-Host "  [FAIL] $nombre -> HTTP $($resultado.status) : $($resultado.content)" -ForegroundColor Red; $script:fail++ }
}

Write-Host "================ FLUJO DE INTEGRACION AGENDATY ================" -ForegroundColor Cyan

# 1. Registro admin (seed ya existe, pero probamos registro público)
Write-Host "`n1. Registro de ciudadano público (RA1)" -ForegroundColor Cyan
$r = Invoke "POST" "/auth/registro" @{ nombre="Juan Perez"; correo="juan@email.com"; password="Cli123*"; documento="12345678"; telefono="3001234567"; rol="ROLE_CIUDADANO" } $null
Check "registro ciudadano", $r, 201
if ($r.status -eq 201) { $ciudadanoId = $r.obj.id; Write-Host "  ciudadano id=$ciudadanoId" }

# 2. Login admin (seed)
Write-Host "`n2. Login admin (RA2)" -ForegroundColor Cyan
$lr = Invoke "POST" "/auth/login" @{ correo="admin@turnos.local"; password="Admin123*" } $null
Check "login admin", $lr, 200
if ($lr.status -eq 200) {
    $token = $lr.obj.token
    $authUserId = $lr.obj.id
    $headers = @{ Authorization = "Bearer $token"; "X-Usuario-Id" = $authUserId }
    Write-Host "  admin id=$authUserId, rol=$($lr.obj.rol)"
}

# 3. Tipos de trámite
Write-Host "`n3. Tipos de trámite" -ForegroundColor Cyan
$t1 = Invoke "POST" "/tipos-tramite" @{ nombre="Cedula"; codigo="CED"; esPrioritarioPorDefecto=$false; activo=$true } $headers
Check "crear tipo Cedula", $t1, 201
$t2 = Invoke "POST" "/tipos-tramite" @{ nombre="Pago servicios"; codigo="PAG"; esPrioritarioPorDefecto=$true; activo=$true } $headers
Check "crear tipo Pago (prioritario)", $t2, 201

# 4. Asesor
Write-Host "`n4. Asesor (RA4)" -ForegroundColor Cyan
$asesor = Invoke "POST" "/admin/usuarios" @{ nombre="Maria Asesora"; correo="asesor@email.com"; password="Ase123*"; documento="87654321"; telefono="3009876543"; rol="ROLE_ASESOR" } $headers
Check "crear asesor", $asesor, 201
if ($asesor.status -eq 201) { $asesorId = $asesor.obj.id; Write-Host "  asesor id=$asesorId" }

# 5. Turno normal (recepcionista) CA1-CA4
Write-Host "`n5. Turno NORMAL (recepcionista) CA1-CA4" -ForegroundColor Cyan
$tn = Invoke "POST" "/turnos?usuarioId=1" @{ ciudadanoId=$ciudadanoId; tipoTramiteId=$t1.obj.id; prioritario=$false } $headers
Check "generar turno normal", $tn, 201
$turnoNormalId = $tn.obj.id
Write-Host "  $($tn.obj.numeroTurno) | estado=$($tn.obj.estado) | prioridad=$($tn.obj.prioridad)"

# 6. Turno prioritario
Write-Host "`n6. Turno PRIORITARIO" -ForegroundColor Cyan
$tp = Invoke "POST" "/turnos?usuarioId=1" @{ ciudadanoId=$ciudadanoId; tipoTramiteId=$t2.obj.id; prioritario=$true } $headers
Check "generar turno prioritario", $tp, 201
$turnoPrioritarioId = $tp.obj.id
Write-Host "  $($tp.obj.numeroTurno) | estado=$($tp.obj.estado) | prioridad=$($tp.obj.prioridad)"

# 7. Llamar siguiente (asesor) RN3: prioritario primero
Write-Host "`n7. Llamar siguiente (asesor) RN3" -ForegroundColor Cyan
$headersAsesor = @{ Authorization = "Bearer $token"; "X-Usuario-Id" = $asesorId }
$llamado = Invoke "PUT" "/turnos/llamar-siguiente?asesorId=$asesorId" $null $headersAsesor
Check "llamar siguiente (prioritario primero)", $llamado, 200
Write-Host "  Llamado: $($llamado.obj.numeroTurno) prioridad=$($llamado.obj.prioridad)"

# 8. Iniciar atención (RN1, RN4)
Write-Host "`n8. Iniciar atención (RN1, RN4)" -ForegroundColor Cyan
$att = Invoke "PUT" "/atenciones/iniciar?turnoId=$($llamado.obj.id)&asesorId=$asesorId" $null $headersAsesor
Check "iniciar atención", $att, 200
Write-Host "  atención id=$($att.obj.id)"

# 8b. Intento iniciar otra atención con mismo asesor (debe fallar RN4)
$att2 = Invoke "PUT" "/atenciones/iniciar?turnoId=$turnoNormalId&asesorId=$asesorId" $null $headersAsesor
Check "asesor con turno activo -> rechazar (RN4)", $att2, 400

# 9. Pausar / reanudar
Write-Host "`n9. Pausar / reanudar" -ForegroundColor Cyan
$p = Invoke "PUT" "/atenciones/$($llamado.obj.id)/pausar?asesorId=$asesorId" $null $headersAsesor
Check "pausar", $p, 200
$r2 = Invoke "PUT" "/atenciones/$($llamado.obj.id)/reanudar?asesorId=$asesorId" $null $headersAsesor
Check "reanudar", $r2, 200

# 10. Finalizar (RN5, RN6)
Write-Host "`n10. Finalizar atención (RN5, RN6)" -ForegroundColor Cyan
$f = Invoke "PUT" "/atenciones/$($llamado.obj.id)/finalizar?asesorId=$asesorId" @{ resultado="Atendido correctamente"; observaciones="Todo en orden" } $headersAsesor
Check "finalizar con resultado", $f, 200
Write-Host "  espera=$($f.obj.tiempoEsperaMinutos)min | atencion=$($f.obj.tiempoAtencionMinutos)min"

# 10b. Finalizar sin resultado (debe fallar RN5)
$f2 = Invoke "PUT" "/atenciones/$turnoNormalId/finalizar?asesorId=$asesorId" @{ resultado=""; observaciones="x" } $headersAsesor
Check "finalizar sin resultado -> rechazar (RN5)", $f2, 400

# 11. No atendido (RN2)
Write-Host "`n11. Marcar no atendido (RN2)" -ForegroundColor Cyan
$na = Invoke "PUT" "/turnos/$turnoNormalId/no-atendido" $null $headers
Check "marcar no atendido", $na, 200
Write-Host "  estado=$($na.obj.estado)"

# 12. Reportes
Write-Host "`n12. Reportes" -ForegroundColor Cyan
$rep = Invoke "GET" "/reportes/tiempo-espera-promedio" $null $headers
Check "reporte tiempo espera", $rep, 200
Write-Host "  promedio=$($rep.obj.valor) min"

$rep2 = Invoke "GET" "/reportes/turnos-atendidos" $null $headers
Check "reporte turnos atendidos", $rep2, 200
Write-Host "  atendidos=$($rep2.obj.atendidos) | no atendidos=$($rep2.obj.no_atendidos)"

$rep3 = Invoke "GET" "/reportes/tramites-frecuentes" $null $headers
Check "reporte tramites frecuentes", $rep3, 200

# 13. Acceso no autorizado (403)
Write-Host "`n13. Acceso no autorizado (403)" -ForegroundColor Cyan
$ciudadanoToken = $null
try {
    $cr = Invoke "POST" "/auth/login" @{ correo="juan@email.com"; password="Cli123*" } $null
    $ciudadanoToken = $cr.obj.token
    $ciudadanoHeaders = @{ Authorization = "Bearer $ciudadanoToken"; "X-Usuario-Id" = $cr.obj.id }
    $noAuth = Invoke "PUT" "/turnos/llamar-siguiente?asesorId=1" $null $ciudadanoHeaders
    Check "ciudadano no puede llamar turno -> 403", $noAuth, 403
} catch {
    Write-Host "  No se pudo hacer login como ciudadano para probar 403"
}

Write-Host "`n================ RESULTADO ================" -ForegroundColor Cyan
Write-Host ("OK: $ok | FALLOS: $fail")
if ($fail -eq 0) { Write-Host "FLUJO COMPLETO: EXITOSO" -ForegroundColor Green } else { Write-Host "FLUJO COMPLETO: CON FALLOS" -ForegroundColor Red }
exit $fail
