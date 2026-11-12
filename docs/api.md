# Referencia de API — Agendaty

Base: `http://localhost:8080` (rutas cortas, sin prefijo `/api/v1`).
Formato de error: `ErrorResponse(timeStamp, status, errorMessage, errorsDetails)`.
Autenticación: header `X-Usuario-Id` en peticiones protegidas. Esquema didáctico (sin JWT).

---

## Autenticación (RA1, RA2)

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| POST | `/auth/registro` | Público | `{nombre, correo, password, documento, telefono}` | `{id, nombre, correo, rol}` | 201, 400 |
| POST | `/auth/login` | Público | `{correo, password}` | `{id, nombre, correo, rol}` | 200, 400 |

---

## Admin (RA4, solo ADMINISTRADOR, header X-Usuario-Id)

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| POST | `/admin/usuarios` | ADMINISTRADOR | `{nombre, correo, password, documento, telefono, rol}` | `{id, nombre, correo, rol}` | 201, 400, 403 |

---

## Turnos

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| POST | `/turnos?usuarioId=X` | RECEPCIONISTA, ADMINISTRADOR | `{ciudadanoId, tipoTramiteId, prioritario}` | `{id, numeroTurno, estado, prioridad, ...}` | 201, 400, 403 |
| GET | `/turnos` | Todos autenticados | — | `[{id, numeroTurno, estado, prioridad, ...}]` | 200, 403 |
| GET | `/turnos/{id}` | Todos autenticados | — | `{id, numeroTurno, estado, ...}` | 200, 404 |
| PUT | `/turnos/llamar-siguiente?asesorId=X` | ASESOR | — | `{id, numeroTurno, estado, prioridad, ...}` | 200, 400, 403, 404 |
| PUT | `/turnos/{id}/cancelar?usuarioId=X` | RECEPCIONISTA, ADMINISTRADOR | — | `{id, ..., estado: CANCELADO}` | 200, 400, 403, 404 |
| PUT | `/turnos/{id}/no-atendido` | RECEPCIONISTA, ADMINISTRADOR | — | `{id, ..., estado: NO_ATENDIDO}` | 200, 400, 403, 404 |

---

## Atención

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| PUT | `/atenciones/iniciar?turnoId=X&asesorId=Y` | ASESOR | — | `{id, turnoId, fechaInicio, ...}` | 200, 400, 403, 404, 409 |
| PUT | `/atenciones/{turnoId}/pausar?asesorId=Y` | ASESOR | — | `{id, ..., estado: EN_PAUSA}` | 200, 400, 403, 404 |
| PUT | `/atenciones/{turnoId}/reanudar?asesorId=Y` | ASESOR | — | `{id, ..., estado: EN_ATENCION}` | 200, 400, 403, 404 |
| PUT | `/atenciones/{turnoId}/finalizar?asesorId=Y` | ASESOR | `{resultado, observaciones}` | `{id, ..., estado: FINALIZADO, tiempoEspera, tiempoAtencion}` | 200, 400, 403, 404, 409 |
| GET | `/atenciones/turno/{turnoId}` | Todos autenticados | — | `{id, turnoId, ..., resultado, tiempoEspera, tiempoAtencion}` | 200, 404 |

---

## Asesores

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| GET | `/asesores` | Todos autenticados | — | `[{id, nombre, disponible, numeroVentanilla}]` | 200, 403 |
| PUT | `/asesores/{id}/disponible?disponible=true\|false` | ADMINISTRADOR | — | `{id, nombre, disponible}` | 200, 400, 403, 404 |

---

## Ventanillas

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| GET | `/ventanillas` | Todos autenticados | — | `[{id, numero, descripcion, asesorId}]` | 200, 403 |
| POST | `/ventanillas` | ADMINISTRADOR | `{numero, descripcion}` | `{id, numero, descripcion}` | 201, 400, 403 |
| PUT | `/ventanillas/{id}` | ADMINISTRADOR | `{numero, descripcion}` | `{id, numero, descripcion}` | 200, 400, 403, 404 |
| DELETE | `/ventanillas/{id}` | ADMINISTRADOR | — | — | 204, 403, 404 |

---

## Tipos de trámite

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| GET | `/tipos-tramite` | Todos autenticados | — | `[{id, nombre, codigo, prioritarioPorDefecto, activo}]` | 200, 403 |
| POST | `/tipos-tramite` | ADMINISTRADOR | `{nombre, codigo, prioritarioPorDefecto}` | `{id, nombre, codigo, ...}` | 201, 400, 403 |
| PUT | `/tipos-tramite/{id}` | ADMINISTRADOR | `{nombre, codigo, prioritarioPorDefecto, activo}` | `{id, nombre, codigo, ...}` | 200, 400, 403, 404 |

---

## Reportes

| Método | Ruta | Roles | Request | Response | Códigos |
|--------|------|-------|---------|----------|---------|
| GET | `/reportes/tiempo-espera-promedio` | COORDINADOR, ADMINISTRADOR | `fechaDesde, fechaHasta` | `{promedioMinutos, cantidad}` | 200, 403 |
| GET | `/reportes/turnos-atendidos` | COORDINADOR, ADMINISTRADOR | `fechaDesde, fechaHasta` | `{atendidos, noAtendidos, total}` | 200, 403 |
| GET | `/reportes/tramites-frecuentes` | COORDINADOR, ADMINISTRADOR | `fechaDesde, fechaHasta` | `[{tipoTramite, cantidad}]` | 200, 403 |

---

## Códigos HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK |
| 201 | Creado |
| 204 | Sin contenido (eliminación) |
| 400 | Solicitud incorrecta / regla de negocio violada |
| 401 | No autenticado (credenciales inválidas) |
| 403 | No autorizado (rol insuficiente) |
| 404 | Recurso no encontrado |
| 409 | Conflicto (turno ya tomado, asesor con turno activo, etc.) |
