# Decisions — Agendaty

## ADR-001: Simplicidad sobre sofisticación
Se elimina JWT, Spring Security complejo, Docker, mappers complejos y auditoría avanzada. Se usa Controller → Service → Repository → Entity con `@CrossOrigin` por controller.

## ADR-002: Estados del turno
Se incluyen EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO (según nota de consistencia del brief).

## ADR-003: Numeración de turnos
Formato `{CODIGO_TRAMITE}{P|N}-{NNNNN}` con consecutivo diario y restricción `unique` en BD.

## ADR-004: Seguridad
Login básico con BCrypt, sin JWT. Rol verificado en service/controller. No hay tareas programadas para RN2; se usa endpoint explícito.

## ADR-005: Rutas
Rutas cortas (`/turnos`, `/asesores`, etc.) sin prefijo `/api/v1`.

## Supuestos abiertos
- ¿Base de datos producción: PostgreSQL o MySQL?
- ¿Se necesita notificación en tiempo real (WebSocket) para el coordinador o basta polling?
