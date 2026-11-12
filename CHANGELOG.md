# Changelog — Agendaty

## [1.0.0] — Versión completa (2026-11-12)

### ✨ Nuevas funcionalidades

- **Autenticación y roles (RA1-RA7)**: registro público como ciudadano, login con correo+contraseña, admin sembrado automáticamente, contraseñas con BCrypt, y autorización por rol mediante header `X-Usuario-Id`.
- **Gestión de usuarios internos (RA4)**: el administrador puede registrar asesores, recepcionistas, coordinadores y otros administradores mediante `POST /admin/usuarios`.
- **Semilla de administrador (RA3)**: al arrancar el backend se crea automáticamente un administrador si no existe uno.
- **Frontend completo (Fase 7)**: landing page, login, registro y dashboards por rol (recepcionista, asesor, coordinador, administrador, ciudadano, reportes) funcionando en modo mock y con backend real.
- **Máquina de estados completa**: EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO.

### 🔧 Mejoras

- **Backend simplificado**: arquitectura Controller → Service → Repository → Entity, con `@CrossOrigin` en cada controller y rutas cortas (sin prefijo `/api/v1`).
- **CORS explícito**: configurado por controller con origen `http://localhost:5173`.
- **Pruebas unitarias**: 44 pruebas JUnit 5 + Mockito, 100% verde, cubriendo RN1-RN6, CA1-CA5 y RA1-RA6.
- **Integración verificada**: flujo completo probado con curl/PowerShell (registro, login, tipos de trámite, asesor, turnos normal y prioritario, llamar siguiente, atención iniciar/pausar/reanudar/finalizar, no atendido, reportes).

### 🐛 Correcciones

- **Frontend**: `navigate()` movido a `useEffect` para evitar actualizaciones en fase de render.
- **Frontend**: proxy de Vite eliminado para evitar conflictos con peticiones estáticas.
- **Backend**: eliminación de JWT, Spring Security completo, Docker y mappers complejos para simplicidad.

---

## [0.0.1] — Línea de base inicial

- Entidades `Usuario`, `Asesor`, `Ciudadano`, `Ventanilla` con herencia `JOINED`.
- CRUD de ciudadanos, asesores y ventanillas.
- Manejador de excepciones global con `ErrorResponse`.
- 37 pruebas unitarias iniciales.
