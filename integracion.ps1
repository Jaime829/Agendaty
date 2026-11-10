$Base = "http://localhost:8080/api/v1"
$ErrorActionPreference = "Continue"

function Invoke($method, $path, $body, $auth) {
    $params = @{
        Uri = "$Base$path"; Method = $method; TimeoutSec = 15; ErrorAction = "Stop"
    }
    if ($body) { $params.ContentType = "application/json"; $params.Body = ($body | ConvertTo-Json -Depth 5) }
    if ($auth) { $params.Headers = @{ Authorization = "Bearer $auth" } }
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

# 1. Registro admin
Write-Host "`n1. Registro de administrador" -ForegroundColor Cyan
$r = Invoke "POST" "/auth/register" @{ nombre="Admin"; username="admin"; password="admin123"; email="admin@mail.com"; rol="ROLE_ADMINISTRADOR" } $null
if ($r.status -eq 409) { $lr = Invoke "POST" "/auth/login" @{ username="admin"; password="admin123" } $null; $token = $lr.obj.token; Write-Host "  Admin ya existe, login OK" -ForegroundColor Yellow }
else { $token = $r.obj.token; Write-Host "  Admin registrado" -ForegroundColor Green }
Check "registro/login admin", @{ status = if($token){200}else{400} }, 200

# 2. Tipos de tramite
Write-Host "`n2. Tipos de tramite" -ForegroundColor Cyan
$t1 = Invoke "POST" "/tipos-tramite" @{ nombre="Cedula"; codigo="CED"; esPrioritarioPorDefecto=$false; activo=$true } $token
Check "crear tipo Cedula", $t1, 201
$t2 = Invoke "POST" "/tipos-tramite" @{ nombre="Pago servicios"; codigo="PAG"; esPrioritarioPorDefecto=$true; activo=$true } $token
Check "crear tipo Pago (prioritario)", $t2, 201
Write-Host "  CED id=$($t1.obj.id) | PAG id=$($t2.obj.id)"

# 3. Ciudadano
Write-Host "`n3. Ciudadano" -ForegroundColor Cyan
$c = Invoke "POST" "/usuarios" @{ nombre="Juan Perez"; username="juan"; password="juan123"; email="juan@mail.com"; rol="ROLE_CIUDADANO" } $token
Check "crear ciudadano", $c, 201
$ciudadanoId = $c.obj.id
Write-Host "  ciudadano id=$ciudadanoId"

# 4. Turno normal (recepcionista) CA1-CA4
Write-Host "`n4. Turno NORMAL (recepcionista)" -ForegroundColor Cyan
$tn = Invoke "POST" "/turnos?usuarioId=1" @{ ciudadanoId=$ciudadanoId; tipoTramiteId=$t1.obj.id; prioritario=$false } $token
Check "generar turno normal", $tn, 201
$turnoNormalId = $tn.obj.id
Write-Host "  $($tn.obj.numeroTurno) | estado=$($tn.obj.estado) | prioridad=$($tn.obj.prioridad)"

# 5. Turno prioritario
Write-Host "`n5. Turno PRIORITARIO (recepcionista)" -ForegroundColor Cyan
$tp = Invoke "POST" "/turnos?usuarioId=1" @{ ciudadanoId=$ciudadanoId; tipoTramiteId=$t2.obj.id; prioritario=$true } $token
Check "generar turno prioritario", $tp, 201
$turnoPrioritarioId = $tp.obj.id
Write-Host "  $($tp.obj.numeroTurno) | estado=$($tp.obj.estado) | prioridad=$($tp.obj.prioridad)"

# 6. Cola
Write-Host "`n6. Cola de turnos" -ForegroundColor Cyan
$lista = Invoke "GET" "/turnos" $null $token
Check "listar turnos", $lista, 200
$lista.obj | ForEach-Object { Write-Host "  $($_.numeroTurno) - $($_.estado) - prioridad=$($_.prioridad)" }

# 7. Asesor
Write-Host "`n7. Asesor" -ForegroundColor Cyan
Invoke "POST" "/usuarios" @{ nombre="Maria Asesora"; username="asesor1"; password="asesor123"; email="a@mail.com"; rol="ROLE_ASESOR" } $token | Out-Null
$asesorLista = Invoke "GET" "/usuarios" $null $token
$asesorId = ($asesorLista.obj | Where-Object { $_.username -eq "asesor1" }).id
Write-Host "  asesor id=$asesorId"

# 8. Llamar siguiente - RN3 prioridad
Write-Host "`n8. Llamar siguiente - RN3 prioridad primero" -ForegroundColor Cyan
$llamado = Invoke "PUT" "/turnos/llamar-siguiente?asesorId=$asesorId" $null $token
Check "llamar siguiente (prioritario primero)", $llamado, 200
Write-Host "  Llamado: $($llamado.obj.numeroTurno) prioridad=$($llamado.obj.prioridad) (debe ser PRIORITARIO)"

# 9. Iniciar atencion RN1 RN4
Write-Host "`n9. Iniciar atencion (RN1, RN4)" -ForegroundColor Cyan
$att = Invoke "PUT" "/atenciones/iniciar?turnoId=$($llamado.obj.id)&asesorId=$asesorId" $null $token
Check "iniciar atencion", $att, 200
Write-Host "  atencion id=$($att.obj.id) | turno=$($att.obj.numeroTurno)"

# mismo asesor con turno activo -> rechazar RN4
$att2 = Invoke "PUT" "/atenciones/iniciar?turnoId=$turnoNormalId&asesorId=$asesorId" $null $token
Check "asesor con turno activo -> rechazar (RN4)", $att2, 400

# 10. Pausar / reanudar
Write-Host "`n10. Pausar / reanudar atencion" -ForegroundColor Cyan
$p = Invoke "PUT" "/atenciones/$($llamado.obj.id)/pausar?asesorId=$asesorId" $null $token
Check "pausar atencion", $p, 200
$r2 = Invoke "PUT" "/atenciones/$($llamado.obj.id)/reanudar?asesorId=$asesorId" $null $token
Check "reanudar atencion", $r2, 200

# 11. Finalizar RN5 RN6
Write-Host "`n11. Finalizar atencion (RN5, RN6)" -ForegroundColor Cyan
$f = Invoke "PUT" "/atenciones/$($llamado.obj.id)/finalizar?asesorId=$asesorId" @{ resultado="Atendido correctamente"; observaciones="Todo en orden" } $token
Check "finalizar con resultado", $f, 200
Write-Host "  resultado=$($f.obj.resultado) | espera=$($f.obj.tiempoEsperaMinutos)min | atencion=$($f.obj.tiempoAtencionMinutos)min | pausas=$($f.obj.totalPausasMinutos)min"

# finalizar sin resultado -> rechazar RN5
$f2 = Invoke "PUT" "/atenciones/$turnoNormalId/finalizar?asesorId=$asesorId" @{ resultado=""; observaciones="x" } $token
Check "finalizar sin resultado -> rechazar (RN5)", $f2, 400

# 12. No atendido RN2
Write-Host "`n12. Marcar no atendido (RN2)" -ForegroundColor Cyan
$na = Invoke "PUT" "/turnos/$turnoNormalId/no-atendido" $null $token
Check "marcar no atendido", $na, 200
Write-Host "  estado=$($na.obj.estado) (debe ser NO_ATENDIDO)"

# 13. Reportes
Write-Host "`n13. Reportes" -ForegroundColor Cyan
$rep = Invoke "GET" "/reportes/tiempo-espera-promedio" $null $token
Check "reporte tiempo espera promedio", $rep, 200
Write-Host "  promedio=$($rep.obj.valor) min | atenciones=$($rep.obj.cantidad_atenciones)"

$rep2 = Invoke "GET" "/reportes/turnos-atendidos" $null $token
Check "reporte turnos atendidos", $rep2, 200
Write-Host "  atendidos=$($rep2.obj.atendidos) | no atendidos=$($rep2.obj.no_atendidos) | total=$($rep2.obj.total)"

$rep3 = Invoke "GET" "/reportes/tramites-frecuentes" $null $token
Check "reporte tramites frecuentes", $rep3, 200
$rep3.obj | ForEach-Object { Write-Host "  $($_.tipoTramiteNombre): $($_.cantidad)" }

# 14. Acceso no autorizado 403
Write-Host "`n14. Acceso no autorizado (403)" -ForegroundColor Cyan
$cr = Invoke "POST" "/auth/login" @{ username="juan"; password="juan123" } $null
$ciudadanoToken = $cr.obj.token
if ($ciudadanoToken) {
  $noAuth = Invoke "PUT" "/turnos/llamar-siguiente?asesorId=1" $null $ciudadanoToken
  Check "ciudadano no puede llamar turno -> 403", $noAuth, 403
}

Write-Host "`n================ RESULTADO ================" -ForegroundColor Cyan
Write-Host ("OK: $ok | FALLOS: $fail")
if ($fail -eq 0) { Write-Host "FLUJO COMPLETO: EXITOSO" -ForegroundColor Green } else { Write-Host "FLUJO COMPLETO: CON FALLOS" -ForegroundColor Red }
exit $fail
