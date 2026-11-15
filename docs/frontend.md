# Documentación del Frontend — Agendaty

Rediseñado el 2026-11-15 (rama `feature/frontend-redesign`). Ver `docs/frontend-audit.md` para la auditoría previa y `docs/decisions.md` (ADR-011 a ADR-015) para las decisiones.

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
