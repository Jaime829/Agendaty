# Referencia de API — Agendaty

Base: `http://localhost:8080/api/v1` (configurable por perfil/variable de entorno).
Formato de error: `ErrorResponse(timeStamp, status, errorMessage, errorsDetails)`.
Autenticación: JWT Bearer en header `Authorization: Bearer <token>`.

---

## Autenticación

| Método | Ruta | Roles | Cuerpo request | Respuesta (200/201) | Descripción |
|--------|------|-------|----------------|---------------------|-------------|
| POST | `/api/v1/auth/login` | Público | `LoginRequest` | `LoginResponse` | Autentica y devuelve JWT |
| POST | `/api/v1/auth/register` | Público | `RegisterRequest` | `LoginResponse` | Registra usuario (ciudadano/admin) |

---

## Tipos de trámite

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/v1/tipos-tramite` | Público | Listar tipos de trámite (CA1) |
| GET | `/api/v1/tipos-tramite/{id}` | Cualquier rol autenticado | Obtener tipo de trámite |
| POST | `/api/v1/tipos-tramite` | `ADMINISTRADOR` | Crear tipo de trámite |
| PUT | `/api/v1/tipos-tramite/{id}` | `ADMINISTRADOR` | Actualizar tipo de trámite |
| DELETE | `/api/v1/tipos-tramite/{id}` | `ADMINISTRADOR` | Eliminar tipo de trámite |

---

## Turnos (Solicitudes / Turnos)

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| POST | `/api/v1/turnos?usuarioId=X` | `RECEPCIONISTA`, `ADMINISTRADOR` | Generar turno (CA1-CA4) |
| GET | `/api/v1/turnos` | `RECEPCIONISTA`, `ASESOR`, `COORDINADOR`, `ADMINISTRADOR` | Listar turnos (filtros opcionales: estado, prioridad, ciudadanoId, tipoTramiteId) |
| GET | `/api/v1/turnos/{id}` | Cualquier autenticado | Obtener turno |
| PUT | `/api/v1/turnos/llamar-siguiente?asesorId=X` | `ASESOR` | Llamar siguiente (CA5, RN3) |
| PUT | `/api/v1/turnos/{id}/cancelar?usuarioId=X` | `RECEPCIONISTA`, `ADMINISTRADOR` | Cancelar turno |
| PUT | `/api/v1/turnos/{id}/no-atendido` | `RECEPCIONISTA`, `ADMINISTRADOR` | Marcar no atendido (RN2) |

---

## Atención

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| PUT | `/api/v1/atenciones/iniciar?turnoId=X&asesorId=Y` | `ASESOR` | Iniciar atención (RN1, RN4) |
| PUT | `/api/v1/atenciones/{turnoId}/pausar?asesorId=Y` | `ASESOR` | Pausar atención |
| PUT | `/api/v1/atenciones/{turnoId}/reanudar?asesorId=Y` | `ASESOR` | Reanudar atención |
| PUT | `/api/v1/atenciones/{turnoId}/finalizar?asesorId=Y` | `ASESOR` | Finalizar con resultado (RN5, RN6) |
| GET | `/api/v1/atenciones/turno/{turnoId}` | Cualquier autenticado | Obtener atención por turno |

---

## Asesores

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/v1/asesores` | `ASESOR`, `RECEPCIONISTA`, `COORDINADOR`, `ADMINISTRADOR` | Listar asesores |
| GET | `/api/v1/asesores/{id}` | `ASESOR`, `RECEPCIONISTA`, `COORDINADOR`, `ADMINISTRADOR` | Obtener asesor |
| PUT | `/api/v1/asesores/{id}/disponible?disponible=true\|false` | `ADMINISTRADOR` | Cambiar disponibilidad |

---

## Ventanillas (heredadas de la línea de base)

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/v1/ventanillas` | `ASESOR`, `RECEPCIONISTA`, `COORDINADOR`, `ADMINISTRADOR` | Listar ventanillas |
| POST | `/api/v1/ventanillas` | `ADMINISTRADOR` | Crear ventanilla |
| PUT | `/api/v1/ventanillas/{id}` | `ADMINISTRADOR` | Actualizar ventanilla |
| DELETE | `/api/v1/ventanillas/{id}` | `ADMINISTRADOR` | Eliminar ventanilla |

---

## Reportes

| Método | Ruta | Roles | Filtros opcionales | Descripción |
|--------|------|-------|--------------------|-------------|
| GET | `/api/v1/reportes/tiempo-espera-promedio` | `COORDINADOR`, `ADMINISTRADOR` | fechaDesde, fechaHasta, asesorId, tipoTramiteId | Tiempo promedio de espera (RN6) |
| GET | `/api/v1/reportes/turnos-atendidos` | `COORDINADOR`, `ADMINISTRADOR` | fechaDesde, fechaHasta, asesorId, tipoTramiteId | Turnos atendidos / no atendidos |
| GET | `/api/v1/reportes/tramites-frecuentes` | `COORDINADOR`, `ADMINISTRADOR` | fechaDesde, fechaHasta, tipoTramiteId | Trámites frecuentes |

---

## Usuarios (Administración)

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| GET | `/api/v1/usuarios` | `ADMINISTRADOR` | Listar usuarios |
| POST | `/api/v1/usuarios` | `ADMINISTRADOR` | Crear usuario con rol |
| PUT | `/api/v1/usuarios/{id}/rol` | `ADMINISTRADOR` | Asignar rol |

---

## Códigos HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK |
| 201 | Creado |
| 204 | Sin contenido (eliminación) |
| 400 | Solicitud incorrecta / regla de negocio violada |
| 401 | No autenticado |
| 403 | No autorizado (rol insuficiente) |
| 404 | Recurso no encontrado |
| 409 | Conflicto de integridad (ej. documento duplicado) |

---

## DTOs principales (resumen)

- `TurnoRequest`: `ciudadanoId`, `tipoTramiteId`, `prioritario`
- `TurnoResponse`: `id`, `numeroTurno`, `estado`, `prioridad`, `fechaCreacion`, `fechaLlamado`, `fechaFinEspera`, `ciudadanoId`, `ciudadanoNombre`, `tipoTramiteId`, `tipoTramiteNombre`, `asesorId`, `asesorNombre`
- `AtencionRequest`: `resultado` (obligatorio), `observaciones`
- `AtencionResponse`: `id`, `turnoId`, `numeroTurno`, `asesorId`, `asesorNombre`, `fechaInicio`, `fechaFin`, `observaciones`, `resultado`, `tiempoEsperaMinutos`, `tiempoAtencionMinutos`, `totalPausasMinutos`
- `LoginRequest`: `username`, `password`
- `LoginResponse`: `token`, `tipo`, `usuarioId`, `username`, `rol`
