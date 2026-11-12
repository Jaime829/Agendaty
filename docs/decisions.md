# Decisions — Agendaty

## ADR-001: Simplicidad sobre sofisticación
Se elimina JWT, Spring Security completo, Docker, mappers complejos y auditoría avanzada. Se usa Controller → Service → Repository → Entity con `@CrossOrigin` por controller.

## ADR-002: Estados del turno
Se incluyen EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO (según nota de consistencia del brief).

## ADR-003: Numeración de turnos
Formato `{CODIGO_TRAMITE}{P|N}-{NNNNN}` con consecutivo diario y restricción `unique` en BD.

## ADR-004: Seguridad (RA1-RA7)
Login básico con BCrypt, sin JWT. Rol verificado en service/controller mediante header `X-Usuario-Id`. Esquema didáctico; en producción se usaría JWT/Spring Security.

## ADR-005: Rutas
Rutas cortas (`/turnos`, `/asesores`, etc.) sin prefijo `/api/v1`.

## ADR-006: Registro público (RA1)
El rol por defecto SIEMPRE es CIUDADANO. El backend ignora cualquier rol enviado en el registro público.

## ADR-007: Admin seed (RA3)
`CommandLineRunner` que crea un administrador automáticamente al arrancar si no existe. Credenciales desde `ADMIN_EMAIL`/`ADMIN_PASSWORD` (variables de entorno o `application.properties`). Solo para desarrollo.

## ADR-008: Gestión de usuarios internos (RA4)
Solo ADMINISTRADOR puede registrar asesores, recepcionistas, coordinadores y otros administradores mediante `POST /admin/usuarios`. El registro público solo crea ciudadanos.

## ADR-009: RN2 sin tareas programadas
No hay tareas programadas (`@Scheduled`) para marcar turnos como NO_ATENDIDO. Se usa un endpoint explícito `/turnos/{id}/no-atendido` que llama la recepcionista o el admin.

## ADR-010: RN1/RN4 con bloqueo simple
Validación en service dentro de `@Transactional`. Si se necesita protección extra bajo concurrencia, se usa `@Version` en `Turno`.

## Supuestos abiertos
- Base de datos producción: PostgreSQL o MySQL (por definir).
- Notificación en tiempo real para el coordinador: no se usa WebSocket; basta con polling cada 5 segundos en el frontend.
- El seed del admin usa valores por defecto solo para desarrollo: `admin@turnos.local` / `Admin123*`.
