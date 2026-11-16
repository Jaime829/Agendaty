# Documentación del Frontend — Agendaty

Rediseñado el 2026-11-15 (rama `fix/navbar-sesion-activa`, hereda de `feature/dashboard-redesign`). Ver `docs/frontend-audit.md`, `docs/dashboard-audit.md` y `docs/decisions.md` para el historial.

## Sesión y navegación pública (2026-11-15, `fix/navbar-sesion-activa`)

- **Sin sesión**, el header de la landing muestra: «Iniciar sesión» y «Crear cuenta»; hero y CTA final muestran «Crear mi cuenta» + «Iniciar sesión» como antes.
- **Con sesión**, esas llamadas a la acción desaparecen en toda la landing (header, hero y CTA final). En su lugar aparece **«Ir a mi panel»** → ruta del rol actual y un **menú de usuario** desplegable (iniciales, nombre, rol, **Cerrar sesión**) en el header — el mismo `UserMenu` que usa la navbar de la app.
- **Fuente única de verdad de la sesión**: `AuthContext` expone `user`, `loading`, `isAuthenticated`, `tieneRol`, `tieneAlgunaRol`, `login`, `logout`. `authService.getUser()` devuelve `null` (y limpia localStorage) si el dato está corrupto, en vez de romper la app.
- **Panel por rol definido en un único sitio**: `getHomeByRole(rol)` en `src/utils/roleRoutes.ts` (`CIUDADANO → /ciudadano`, `RECEPCIONISTA → /recepcionista`, `ASESOR → /asesor`, `COORDINADOR → /coordinador`, `ADMINISTRADOR → /administrador`). Reutilizado por `PublicNav`, landing, `Navbar`, `LoginPage`, `ProtectedRoute` y `useRolGuard`.
- **Rutas públicas con sesión activa** (`/login`, `/registro`): redirigen con `replace` al panel del rol. Sin sesión, todo sigue igual.
- **Navbar de la app** (`Navbar.tsx`) oculta en rutas públicas; muestra enlaces del rol + `UserMenu` + hamburguesa móvil; el cierre de sesión vuelve a la landing.
- **Sincronía entre pestañas** (mismo origen): `AuthContext` reescucha `storage` y `focus`; si cierras sesión en una pestaña, las demás actualizan su UI (botones de login reaparecen) sin recargar.
- **Cierre de sesión**: limpia estado y localStorage, vuelve a `/` y muestra toast «Sesión cerrada» (Sonner). Desde un panel protegido, redirige a `/` con `replace`.
- **Sin parpadeo**: la sesión se restaura de forma síncrona al montar el proveedor; mientras tanto, CTAs muestran un placeholder del mismo tamaño (nunca login ni panel).
- `Loading` es constante `false` con restauración síncrona; si en el futuro se vuelve asíncrono, el placeholder ya está implementado en `PublicNav`/Landing.

Rediseñado el 2026-11-15 (rama `feature/dashboard-redesign`, hereda del rediseño global `feature/frontend-redesign`). Ver `docs/frontend-audit.md` (auditoría global) y `docs/dashboard-audit.md` (auditoría del dashboard de Administración) así como `docs/decisions.md` (ADR-011 a ADR-019) para las decisiones.

## Novedades del dashboard de Administración (Fase 3, 2026-11-15)

- **KPIs derivados en cliente** (cero endpoints nuevos): Usuarios totales con desglose (`ciudadanos · internos`), Ciudadanos, Personal interno, Asesores. Tabla de usuarios protagonista: búsqueda por correo/nombre, filtro por rol, orden por columna, paginación 10/página, «Mostrando X–Y de Z». Las acciones de fila se **omiten** porque el contrato REST no soporta editar/desactivar/restablecer contraseña (ver ADR-016).
- **Tabs Usuarios | Ventanillas | Tipos de trámite** con CRUD completo (crear, editar, eliminar con confirmación destructiva) usando los endpoints existentes (`/ventanillas`, `/tipos-tramite`).
- **Drawer lateral "Nuevo usuario"** (no más formulario siempre abierto): grid 2 columnas, ayudas bajo campos, mostrar/ocultar contraseña, leyenda «* obligatorio».
- **Navbar limpia**: menú de usuario desplegable (iniciales, correo, rol, Cerrar sesión) con `aria-expanded`/`aria-controls`/`aria-haspopup`, Escape y click fuera; sin botón «Salir» suelto.
- **Componentes compartidos nuevos** (`src/components/`): `PageHeader`, `KpiCard`, `DataTable` (genérica con búsqueda/filtro/orden/paginación y caption), `Drawer` (panel lateral/móvil), `RolBadge` (color + texto por rol).
- **Coordinador**: monitor con PageHeader + KPIs `KpiCard`, cola viva con DataTable (búsqueda + paginación) y sección «Carga por asesor» (se oculta si `GET /asesores` falla — ver nota de backend en ADR-019).
- **Asesor / Recepción / Ciudadano / Reportes**: encabezados unificados con `PageHeader`; Reportes con `KpiCard`.

## Stack

- React 18 + TypeScript + Vite
- Tailwind CSS 3 (sistema de diseño propio, ver «Tokens»)
- React Router DOM (rutas)
- Axios (cliente HTTP)
- Lucide React (iconos)
- Sonner (toasts estandarizados)
- @fontsource-variable/inter (tipografía autocontenida)
- Vitest + Testing Library (pruebas), ESLint flat config

## Estructura

```
frontend/
├── src/
│   ├── api/           # cliente HTTP tipado (auth, turnos, atenciones, asesores, tiposTramite, admin, reportes)
│   ├── components/    # librería de componentes (ver tabla)
│   ├── hooks/         # useRolGuard (redirect por rol)
│   ├── pages/         # vistas por rol (Landing, Login, Registro, Recepcionista, Asesor, Coordinador, Administrador, Ciudadano, Reportes)
│   ├── context/       # AuthContext (sesión persistida en localStorage)
│   ├── routes/        # ProtectedRoute (roles por ruta)
│   ├── types/         # tipos TypeScript (DTOs del backend)
│   ├── test/          # setup de pruebas (cleanup automático)
│   └── utils/         # utilidades (roleRoutes)
├── tailwind.config.js # tokens del sistema de diseño
├── eslint.config.js   # flat config, react-hooks v6
└── vite.config.ts
```

## Sistema de diseño (tokens)

Definidos en `tailwind.config.js`; consumidos como utilidades Tailwind, nunca como hex sueltos en pantallas nuevas.

| Token | Valores | Uso |
|-------|---------|-----|
| `brand-*` (50–900) | base `#175b9c` (600) | acciones primarias, foco, marca |
| `estado-<estado>-{bg,text,dot}` | 7 estados del turno | `BadgeEstado` únicamente |
| `surface` | `#f6f8fb` | fondo de página (`bg-surface`) |
| `fontFamily.sans` | Inter Variable + system fallbacks | todo el texto |
| `fontFamily.mono` | ui-monospace stack | números de turno |
| `radius` | sm 8, md 10, lg 12, xl 16 | inputs md, tarjetas xl |
| `shadow` | `card`, `pop` | tarjetas y overlays |
| `zIndex` | sticky 20, dropdown 30, modal 50, toast 60 | apilamiento explícito |
| `transition` | `out` cubic-bezier(0.16,1,0.3,1); 160/220/320 ms | entradas y focus |
| `animation` | `fade-in`, `rise-in`, `pulse-dot` | motion funcional |

Utilidades propias en `index.css` (`@layer components`): `.card`, `.table-data` (tabla compartida con th uppercase y hover), `.skip-link`, `.page-gutter`.

## Componentes

| Componente | Notas |
|------------|-------|
| `Button` | variantes primary/secondary/destructive/outline/ghost; alturas ≥ 36/44/52 px (área táctil); `loading` con spinner accesible (`aria-busy`) |
| `Input` / `Select` | label asociada con `useId()` (htmlFor/id + aria-invalid/describedby); `hint` y `error` |
| `Card` / `CardHeader` | panel blanco con borde y sombra `card`; API mínima (`titulo`, `descripcion`, `accion`) |
| `BadgeEstado` | punto + etiqueta en español ("En espera", …) con `aria-label="Estado: …"`; `BadgePrioridad` aparte |
| `Alert` | caja única error/success/warning; errores de campo como lista; `role="alert"`/`status` |
| `Modal` / `ConfirmDialog` | `role="dialog" aria-modal`, ESC para cerrar, foco inicial en cerrar, scroll-lock; usado para cancelar turno |
| `Skeleton` / `Loading` | `role="status"` |
| `EmptyState` / `ErrorState` | vacío y error con reintento |
| `Navbar` | sticky; hamburguesa en < lg con `aria-expanded`; `aria-current="page"` en la sección activa; oculta en rutas públicas |

## Vistas por rol

| Rol | Ruta | Vista |
|-----|------|-------|
| Ciudadano | `/ciudadano` | Consulta por número de turno (número grande, estado en vivo `aria-live`) |
| Recepcionista | `/recepcionista` | Generar turno (prioridad normal/prioritaria como radios), cola con cancelar (confirm dialog) |
| Asesor | `/asesor` | Llamar siguiente, iniciar/pausar/reanudar/finalizar; atajos `L I P R F` |
| Coordinador | `/coordinador` | Monitor live (polling 5 s, `tabular-nums`, ordena prioritarios primero), reportes |
| Administrador | `/administrador` | Crear usuarios internos y ciudadanos; tabla de usuarios |
| Reportes | `/reportes` | Filtros por fecha; espera promedio, atendidos, tasa de atención, trámites frecuentes (barras) |

## Accesibilidad (implementada y verificada)

- Skip link «Saltar al contenido principal» (`#contenido`).
- Foco visible global (`outline: 3px brand-600`, offset 2 px) y `:focus-visible` en controles.
- Todas las etiquetas asociadas (htmlFor/id + aria-invalid + aria-describedby).
- Regiones vivas: estado del turno en Asesor/Ciudadano y contadores en Coordinador.
- Tablas con `scope="col"`; navegación con `aria-current`; nav móvil con `aria-expanded`/`aria-controls`.
- `prefers-reduced-motion` desactiva animaciones/transiciones en toda la app.
- Contraste AA (brand-600 #175b9c sobre blanco ≈ 7:1; texto slate-500+ sobre blanco).

## Motion (sutil y funcional)

- Entradas `fade-in`/`rise-in` en hero, modales y toasts.
- Cambio de estado de turno re-monta el badge (transición sutil).
- Hover en filas de tabla y botones; `pulse-dot` en el indicador «en vivo».
- Sin motion decorativo ni que penalice INP; nada que el usuario no pueda desactivar (reduced-motion).

## Pruebas

- `npm run test`: 18 pruebas — BadgeEstado (estados, aria-label, fallback), ProtectedRoute (login/roles), LoginPage, RegistroPage, RecepcionistaPage (genera turno con payload correcto, estado vacío).
- Verificación manual en navegador del recorrido completo de cada rol contra el backend real (login, generar turno, llamar siguiente, iniciar/pausar/reanudar/finalizar atención, consulta ciudadana, reportes y alta de usuarios).

## Comandos

```powershell
cd frontend
npm install
npm run dev      # http://localhost:5173 (con VITE_USE_MOCK=false requiere backend en :8080)
npm run test     # Vitest (18 pruebas)
npm run lint     # ESLint plano, sin warnings
npm run build    # /dist
```

Para desarrollo sin backend puedes usar `VITE_USE_MOCK=true` (login/registro simulados con usuarios de `docs/setup.md`); las demás pantallas requieren el backend.
