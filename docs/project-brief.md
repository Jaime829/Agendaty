# Project Brief — Agendaty

Plataforma de gestión de turnos y atención al ciudadano.

## Problema
Entidad pública atiende usuarios por orden de llegada o listas manuales: congestión, pérdida de turnos, tiempos altos, poca trazabilidad.

## Objetivo
Registrar solicitudes, asignar turnos, gestionar ventanillas/asesores, controlar estados de atención y generar reportes de tiempos de espera.

## Usuarios
- Ciudadano (solicitante)
- Asesor
- Administrador
- Coordinador de atención
- Recepcionista

## Módulos
| Módulo | Funcionalidades |
|--------|-----------------|
| Solicitudes | Registro del trámite |
| Turnos | Generar, llamar, cancelar |
| Atención | Iniciar, pausar, finalizar con observaciones y resultado |
| Asesores | Disponibilidad, ventanilla |
| Estados | EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO |
| Reportes | Tiempo promedio espera, turnos atendidos, trámites frecuentes |

## Reglas de negocio
- RN1: Un turno no atendido por dos asesores al mismo tiempo.
- RN2: Usuario no responde al llamado → NO_ATENDIDO.
- RN3: Prioritarios antes que normales.
- RN4: Un asesor solo tiene un turno activo.
- RN5: Finalizar atención requiere resultado.
- RN6: Calcular tiempo de espera y de atención.

## Historia de usuario (recepcionista)
Como recepcionista, quiero generar un turno según el tipo de trámite.

| CA | Criterio |
|----|----------|
| CA1 | Seleccionar tipo de trámite |
| CA2 | Número de turno único |
| CA3 | Estado "En espera" |
| CA4 | Diferenciar normal/prioritario |
| CA5 | Asesor llama siguiente disponible |

## Matriz de trazabilidad (se completa en Fase 4)

| Req. | Clase/Endpoint | Test |
|------|---------------|------|
| CA1 | TurnoController.generarTurno | — |
| CA2 | TurnoController.generarTurno | — |
| CA3 | TurnoController.generarTurno | — |
| CA4 | TurnoController.generarTurno | — |
| CA5 | TurnoController.llamarSiguiente | — |
| RN1 | AtencionService.iniciarAtencion | — |
| RN2 | TurnoController.marcarNoAtendido | — |
| RN3 | TurnoService.llamarSiguiente | — |
| RN4 | AtencionService.iniciarAtencion | — |
| RN5 | AtencionService.finalizarAtencion | — |
| RN6 | AtencionService.finalizarAtencion | — |
