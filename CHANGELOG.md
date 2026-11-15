# Changelog — Agendaty

## [1.3.0] — Navegación sin login/registro con sesión activa (2026-11-15)

### 🆕 Nuevas funcionalidades

- Con sesión iniciada, **ningún lugar de la app muestra «Iniciar sesión» ni «Registrarse»**: header de la landing, hero y CTA final muestran en su lugar **«Ir a mi panel»** (ruta destino según rol) y un **menú de usuario** desplegable (iniciales, nombre, rol, Cerrar sesión) idéntico al de la navbar de la app.
- **`/login` y `/registro` redireccionan** al panel del rol con sesión (sin dejar historial).
- **Cerrar sesión** vuelve a la landing con toast «Sesión cerrada» y limpia estado y localStorage.
- **Sincronía entre pestañas** del mismo origen vía `storage` + `focus`: en una pestaña cierras sesión, las demás lo detectan y repintan sin recargar.
- **Fuente única de la sesión**: `AuthContext` expone `user/loading/isAuthenticated/tieneRol`; `getHomeByRole` en `src/utils/roleRoutes.ts` decide el panel por rol para navbar, landing, login, guards y redirects.

### 🐛 Correcciones

- **localStorage corrupto ya no rompe la app**: `authService.getUser()` devuelve `null` y limpia atómicamente `user`/`token` si faltan `rol` o el JSON es inválido.
- **Sin parpadeo al recargar (F5)**: la sesión se restaura de forma síncrona; los CTAs muestran placeholder del mismo tamaño mientras tanto.
- **Cerrar sesión desde un panel protegido** sale a la landing en vez de a `/login`.
- **Panel de destino único por rol** definido en un solo sitio (`getHomeByRole`), eliminada la duplicación en login/guards/navbar.

### 🔧 Mejoras

- `PublicNav` y `UserMenu` extraídos como componentes compartidos (reuso estricto entre landing y navbar).
- `Navbar` simplificada: hamburguesa móvil con el mismo set de enlaces + cerrar sesión; sin botones de login/registro con sesión.
- Suite de pruebas: **31 tests** (antes 18) con `PublicNav`, authService tolerante a corrupción y redirects de `/login`/`/registro`.
- `tsc` 0 errores · `lint` 0 warnings · `build` OK.

---

## [1.2.0] — Dashboard de Administración rediseñado (2026-11-15)

### 🆕 Nuevas funcionalidades

- **Dashboard de Administración real**: KPIs (usuarios totales con desglose, ciudadanos, internos, asesores), tabla de usuarios con búsqueda/filtro por rol/orden/paginación, tabs Usuarios · Ventanillas · Tipos de trámite.
- **Gestión completa de ventanillas** (crear, editar, eliminar) y **tipos de trámite** (crear, editar, eliminar con confirmación): nunca antes existieron en la UI.
- **Nuevo usuario en drawer lateral** en vez de formulario siempre abierto; formulario 2 columnas con ayudas y contraseña visible opcional.
- **Componentes compartidos** (`PageHeader`, `KpiCard`, `DataTable` genérica, `Drawer`, `RolBadge`) reutilizados también por Coordinador (monitor + KPIs + carga por asesor), Asesor, Recepción, Ciudadano y Reportes.
- **Navbar limpia**: menú de usuario desplegable (iniciales, correo, rol, Cerrar sesión) con teclado/click fuera y `aria-*`; se eliminó la fila de botones «Módulos» duplicados.

### 🐛 Correcciones

- **Monitor de coordinador resiliente**: si `GET /asesores` falla (el backend lanza NPE cuando un asesor no tiene ventanilla), el panel sigue funcionando y oculta solo «Carga por asesor».
- **Form de alta alineado al backend**: documento y teléfono con patrón `\d`; rol como menú accesible con placeholders claros.
- Sin botones hacia verbos REST inexistentes (PATCH/DELETE de usuarios): las acciones de fila se omiten por contrato.
- Mensajes de error del monitor en español llano (nada de `NullPointerException` en pantalla).

### 🔧 Mejoras

- Entrada sutil de KPIs (`animate-rise-in`, solo transform/opacity, respeta `prefers-reduced-motion`).
- `npm run build` OK; `npm run lint` 0 warnings; `npx tsc --noEmit` 0 errores; tests 18/18 verdes.

---

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
