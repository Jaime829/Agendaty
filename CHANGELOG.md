# Changelog — Agendaty

## [1.1.0] — Rediseño del frontend y ampliación de gestión de usuarios (2026-11-15)

### 🆕 Nuevas funcionalidades

- **Rediseño completo del frontend**: sistema de diseño propio sobre Tailwind (paleta institucional `brand`, tokens de estado de turno, tipografía Inter Variable, radios, sombras, tokens de movimiento) sin modo oscuro por ahora.
- **Landing refinada**: hero con panel de cola ilustrativo, «Cómo funciona», sección de roles, preguntas frecuentes y CTA final, con copy en español llano.
- **Menú móvil accesible**: la barra de navegación ahora es un hamburguesa en pantallas pequeñas (`aria-expanded`, cierre al navegar) en lugar de ocultar los enlaces.
- **Atajos de teclado para el asesor**: `L` llamar siguiente, `I` iniciar, `P` pausar, `R` reanudar, `F` enfocar resultado.
- **Cancelar turno con diálogo de confirmación** (modal accesible con `role="dialog"` y foco gestionado) en lugar de `window.confirm`.
- **Reportes ampliados**: tiempo promedio de espera, turnos atendidos/no atendidos, tasa de atención y trámites frecuentes con barras proporcionales.
- **Toasts estandarizados** con Sonner en toda la app (éxito, conflicto y error).
- **El administrador también crea ciudadanos** desde `/admin/usuarios` (además de asesores, recepcionistas, coordinadores y administradores).

### 🔧 Mejoras

- **Código dividido por ruta** (React.lazy): bundle principal pasa de 327 kB a ~295 kB (≈96 kB gzip); cada pantalla se descarga bajo demanda.
- **Componentes normalizados**: Button, Input, Select, Card, BadgeEstado (punto + etiqueta + `aria-label` para los 7 estados), Skeleton, EmptyState, ErrorState, Alert con errores de campo en caja única.
- **Accesibilidad**: enlace «Saltar al contenido», etiquetas asociadas a inputs/selects en toda la app, `aria-current` en la navegación, regiones vivas (`aria-live`) para cambios de estado del turno, `scope="col"` en tablas, `autocomplete` en formularios, respeto a `prefers-reduced-motion`.
- **Arquitectura backend**: `AuthService` ahora es interfaz con implementación `AuthServiceImp` en `service/impl` (como el resto de los servicios).
- **Sesión restaurada de forma síncrona** desde `localStorage` al arrancar (sin parpadeo de carga).
- **Consultar turnos del asesor sin errores 404**: se deriva el turno activo de `GET /turnos` en vez de una ruta inexistente.
- **Pruebas de frontend**: suite con Vitest + Testing Library (18 pruebas: badge de estado, ruta protegida, login, registro, generar turno).
- **Lint sin warnings**: configuración plana (`eslint.config.js`) con `react-hooks` v6; `npm run lint` limpio.
- **Tests del backend**: 48/48 verdes (se añaden casos: admin crea ciudadano, rol inexistente, duplicados entre ciudadanos y asesores).
- **`npx tsc --noEmit`**: 0 errores.

### 🐛 Correcciones

- **Administrador**: formulario de alta alineado con el backend real (`correo`, `documento`, `teléfono`); antes rechazaba el alta con errores de validación.
- **Reportes**: la pantalla lee los campos reales del backend (`valor`, `cantidad_atenciones`, `no_atendidos`, `tipo_tramite_nombre`); antes mostraba valores vacíos.
- **Docs**: sincronizados con la implementación (`/admin/usuarios` admite `ROLE_CIUDADANO`; payloads reales de `/reportes/*`).
- **Doble envío** en "Iniciar atención" bloqueado durante la transición (sin más PUT duplicados).
- **Carrusel de la cola**: filas de turnos finalizados con badge semántico verde en vez de texto plano.
- **Ciudadano**: al consultar un turno inexistente ya no se confunde el estado de carga con "no encontrado".

---

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
