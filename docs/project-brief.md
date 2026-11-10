# Project Brief — Plataforma de gestión de turnos y atención al ciudadano

> Fuente de verdad del proyecto. Este documento se redacta ANTES de terminar el backend y se convierte en el contrato que guía la implementación. Se basa en el código real del repositorio y en la especificación funcional del usuario.

---

## 1. Problema

Una entidad pública, institución educativa o centro de servicios atiende usuarios por orden de llegada o mediante listas manuales. Esto genera congestión, pérdida de turnos, tiempos de espera altos, poca trazabilidad de la atención y dificultad para medir la calidad del servicio.

## 2. Objetivo

Construir una plataforma que permita registrar solicitudes de atención, asignar turnos, gestionar ventanillas o asesores, controlar estados de atención y generar reportes de tiempos de espera.

## 3. Usuarios

| Rol | Descripción |
|-----|-------------|
| Ciudadano / usuario solicitante | Solicita atención y consulta el estado de su turno |
| Asesor | Atiende turnos en su ventanilla |
| Administrador | Gestiona usuarios, roles, asesores, ventanillas y tipos de trámite |
| Coordinador de atención | Monitorea la cola en tiempo casi real y supervisa la carga por asesor |
| Recepcionista | Genera turnos según tipo de trámite, lista la cola y cancela turnos |

## 4. Módulos sugeridos

| Módulo | Funcionalidades |
|--------|-----------------|
| Solicitudes | Registro del trámite o motivo de atención |
| Turnos | Generación, llamado y cancelación de turnos |
| Atención | Inicio, pausa, finalización y observaciones |
| Asesores | Disponibilidad, ventanillas y carga de atención |
| Estados | En espera, llamado, en atención, finalizado, cancelado, no atendido, en pausa |
| Reportes | Tiempo promedio de espera, turnos atendidos, trámites frecuentes |

## 5. Reglas de negocio (RN)

| ID | Regla |
|----|-------|
| RN1 | Un turno no puede ser atendido por dos asesores al mismo tiempo. |
| RN2 | Si el usuario no responde al llamado, el turno pasa a estado "No atendido". |
| RN3 | Los turnos prioritarios deben pasar antes que los turnos normales. |
| RN4 | Un asesor solo puede tener un turno activo. |
| RN5 | Toda atención finalizada debe tener resultado registrado. |
| RN6 | El sistema debe calcular el tiempo de espera y el tiempo de atención. |

## 6. Modelo de estados del turno (máquina de estados completa)

Nota de consistencia: RN2 introduce el estado **NO_ATENDIDO**, que no estaba en la lista original del módulo Estados, y el módulo Atención menciona "pausa". Ambos se incluyen en el modelo.

Estados: `EN_ESPERA`, `LLAMADO`, `EN_ATENCION`, `EN_PAUSA`, `FINALIZADO`, `CANCELADO`, `NO_ATENDIDO`.

Transiciones permitidas (diseño detallado en `docs/design.md`):
- `EN_ESPERA` → `LLAMADO` (asesor llama siguiente turno, RN3)
- `EN_ESPERA` → `CANCELADO` (recepcionista cancela)
- `LLAMADO` → `EN_ATENCION` (asesor inicia atención, RN1/RN4)
- `LLAMADO` → `NO_ATENDIDO` (usuario no responde, RN2)
- `LLAMADO` → `CANCELADO` (recepcionista cancela)
- `EN_ATENCION` → `EN_PAUSA` (asesor pausa)
- `EN_PAUSA` → `EN_ATENCION` (asesor reanuda)
- `EN_ATENCION` → `FINALIZADO` (asesor finaliza con observaciones y resultado, RN5)
- `EN_PAUSA` → `CANCELADO` (recepcionista cancela)

## 7. Historia de usuario y criterios de aceptación (recepcionista)

**Como recepcionista, quiero generar un turno según el tipo de trámite para organizar la atención de los usuarios.**

| ID | Criterio de aceptación |
|----|------------------------|
| CA1 | El sistema debe permitir seleccionar el tipo de trámite. |
| CA2 | El sistema debe generar un número de turno único. |
| CA3 | El turno debe quedar en estado "En espera". |
| CA4 | El sistema debe diferenciar turnos normales y prioritarios. |
| CA5 | El asesor debe poder llamar el siguiente turno disponible. |

## 8. Existencia en el código (línea de base, Fase 1)

Módulos ya implementados en `com.proyecto.agendaty`:
- **Ciudadanos**: CRUD completo (`CiudadanoController`, `CiudadanoService`/`CiudadanoServiceImp`, `CiudadanoRepository`, `CiudadanoMapper`, DTOs `CiudadanoRequest`/`CiudadanoResponse`/`CiudadanoUpdateRequest`).
- **Asesores**: creación asignando ventanilla (`UsuarioController` → `AsesorService`/`AsesorServiceImp`, `AsesorRepository`, `AsesorMapper`).
- **Ventanillas**: CRUD (`VentanillaController`, `VentanillaService`/`VentanillaServiceImp`, `VentanillaRepository`, `VentanillaMapper`).

Módulos pendientes de implementar: Solicitudes, Turnos, Atención, Estados completos, Reportes, Seguridad/Autenticación.

## 9. Matriz de trazabilidad

| Requisito / Regla / Criterio | Módulo | Clase / Endpoint | Test que lo valida |
|------------------------------|--------|------------------|--------------------|
| CA1 | Turnos | `TurnoController.generarTurno` + `TipoTramite` | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` |
| CA2 | Turnos | `TurnoController.generarTurno` (número único) | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` |
| CA3 | Turnos | `TurnoController.generarTurno` (estado EN_ESPERA) | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` |
| CA4 | Turnos | `TurnoController.generarTurno` (prioritario/normal) | `TurnoServiceImpTest.generarTurno_deberiaDiferenciarPrioritarioYNormal_CA4` |
| CA5 | Turnos | `TurnoController.llamarSiguiente` | `TurnoServiceImpTest.llamarSiguiente_deberiaDevolverElSiguienteTurnoDisponible_CA5` |
| RN1 | Turnos / Atención | `AtencionService.iniciarAtencion` (exclusividad asesor-turno) | `AtencionServiceImpTest.iniciarAtencion_cuandoTurnoYaAtendidoPorOtroAsesor_deberiaRechazar_RN1` |
| RN2 | Turnos | `TurnoService.marcarNoAtendido` | `TurnoServiceImpTest.marcarNoAtendido_cuandoTurnoEstaLlamado_deberiaPasarANO_ATENDIDO_RN2` |
| RN3 | Turnos | `TurnoService.llamarSiguiente` (prioridad primero) | `TurnoServiceImpTest.llamarSiguiente_deberiaPriorizarTurnosPrioritarios_RN3` |
| RN4 | Asesores / Atención | `AtencionService.iniciarAtencion` (un turno activo por asesor) | `TurnoServiceImpTest.llamarSiguiente_cuandoAsesorTieneTurnoActivo_deberiaRechazar_RN4` y `AtencionServiceImpTest.iniciarAtencion_cuandoAsesorYaTieneTurnoActivo_deberiaLanzarExcepcion_RN4` |
| RN5 | Atención | `AtencionService.finalizarAtencion` (resultado obligatorio) | `AtencionServiceImpTest.finalizarAtencion_sinResultado_deberiaLanzarExcepcion_RN5` |
| RN6 | Turnos / Atención | `Turno` / `Atencion` (tiempos de espera y atención) | `AtencionServiceImpTest.finalizarAtencion_deberiaCalcularTiemposEsperaYAtencion_RN6` |

> Matriz completada en Fase 4 con los tests implementados (46 tests, 100% verde).

---

## Convenciones del proyecto (línea de base)

- Java 25, Spring Boot 4.1.1, Maven.
- Paquete base: `com.proyecto.agendaty`.
- Capas: `controller`, `service` (interfaz + impl), `repository`, `entity`, `dto`, `mapper`, `exceptions`.
- Inyección por constructor con `@RequiredArgsConstructor` (Lombok).
- DTOs: `record` para ciudadanos/ventanillas; clases `@Data` para asesores.
- Excepciones controladas por `@RestControllerAdvice` (`GlobalExceptionHandler`) con cuerpo `ErrorResponse`.
- Base de datos H2 en memoria para desarrollo/tests; perfiles por entorno en `application-*.yml`.
