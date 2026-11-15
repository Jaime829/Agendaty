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

## ADR-008: Gestión de usuarios por el administrador (RA4)
Solo ADMINISTRADOR puede registrar asesores, recepcionistas, coordinadores, otros administradores y también ciudadanos mediante `POST /admin/usuarios`. El registro público solo crea ciudadanos (rol forzado CIUDADANO).

## ADR-009: RN2 sin tareas programadas
No hay tareas programadas (`@Scheduled`) para marcar turnos como NO_ATENDIDO. Se usa un endpoint explícito `/turnos/{id}/no-atendido` que llama la recepcionista o el admin.

## ADR-010: RN1/RN4 con bloqueo simple
Validación en service dentro de `@Transactional`. Si se necesita protección extra bajo concurrencia, se usa `@Version` en `Turno`.

## Supuestos abiertos
- Base de datos producción: PostgreSQL o MySQL (por definir).
- Notificación en tiempo real para el coordinador: no se usa WebSocket; basta con polling cada 5 segundos en el frontend.
- El seed del admin usa valores por defecto solo para desarrollo: `admin@turnos.local` / `Admin123*`.

## ADR-011: Sistema de diseño propio en Tailwind (Fase 2, frontend-redesign)
Se mantiene una biblioteca de componentes propia (Button, Input, Select, Card, BadgeEstado, Alert, Modal, ConfirmDialog, Skeleton, EmptyState, ErrorState) en lugar de adoptar shadcn/ui o Radix. Motivos: el proyecto ya tiene ~14 componentes funcionales, el catálogo de shadcn obligaría a instalar Radix + CLI + regenerar código ajeno para un diseño institucional que ningún preset cubre, y ADR-001 prioriza simplicidad. Se justifican dependencias nuevas: `@fontsource-variable/inter` (tipografía institucional autocontenida, sin CDN externa) y `sonner` (toasts estandarizados, ~4 kB, usada vía skill ask-sonner). Recharts se descartó: los gráficos de Reportes se dibujan con CSS/SVG propio (barras) para no sumar ~90 kB a una pantalla secundaria.

## ADR-012: Tokens de diseño en tailwind.config.js
Paleta `brand` (azul institucional `#175b9c`, 600) + `estado-*` semánticos para los 7 estados del turno (mismo hex que los badges, una fuente de verdad), `surface`, radios `sm/md/lg/xl` (8/10/12/16), sombras `card/pop`, z-index (`sticky 20`, `dropdown 30`, `modal 50`, `toast 60`), motion tokens (`out` cubic-bezier(0.16,1,0.3,1), duraciones 160/220/320 ms, `animate-fade-in|rise-in|pulse-dot`), `prefers-reduced-motion` forzado en `index.css`. Sin dark mode por ahora; los tokens quedan listos para añadirlo.

## ADR-013: Modo oscuro fuera de alcance (Fase 1, decisión del autor)
Se decidió no implementar dark mode en la Fase 2–7 del rediseño; los tokens CSS se estructuran para soportarlo después sin tocar componentes.

## ADR-014: Badges de estado con punto + texto en español
`BadgeEstado` muestra punto de color + etiqueta en minúsculas de oración ("En espera", "En atención"…) con `aria-label="Estado: …"`. Los 7 estados usan los tokens `estado-*`. `BadgePrioridad` separado para PRIORITARIO/normal. Estilo inspirado en los GOV.UK tags, sin copiarlos.

## ADR-015: Corrección documental del contrato de reportes (C4)
`docs/api.md` documenta `tiempo-espera-promedio` como `{promedioMinutos, cantidad}`, pero el backend implementa (y el frontend consume) `{metrica, valor, cantidad_atenciones, fecha_desde, fecha_hasta}`. Se corrigen los docs para reflejar la implementación real; no se cambia ni el backend ni el contrato de API.
