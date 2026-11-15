# Auditoría de Frontend — Agendaty (Fase 1)

Fecha: 2026-11-15 · Rama: `feature/frontend-redesign` · Estado: línea base sobre la que se trabaja y se compara en Fase 6.

Entorno verificado real: frontend `http://localhost:5173` (Vite, `VITE_USE_MOCK=false`) + backend `http://localhost:8080`.
Datos sembrados en H2 para la auditoría: admin seed (`admin@turnos.local`) y usuarios creados vía panel admin: `asesor@turnos.local` (ASESOR), `recepcion@turnos.local` (RECEPCIONISTA), `coord@turnos.local` (COORDINADOR), `juan@email.com` (CIUDADANO). Turno de prueba `CEDN-00001` recorrido end-to-end (EN_ESPERA → LLAMADO → EN_ATENCION → EN_PAUSA → EN_ATENCION → FINALIZADO) sin errores de red.

## Referencias visuales (inspiración, nunca copia)

1. **GOV.UK Design System** — https://design-system.service.gov.uk/patterns/ — Patrones adoptados como principios: *Error summary* (caja-resumen de errores de campo), *Tag* (etiquetas de estado en minúsculas/oración), *Notification banner*, espaciado claro, etiquetas siempre visibles y mensajes directos en español llano. También el espíritu de *task list* y foco altísimo en teclado/lector.
2. **Medical Queue Management Dashboard** (Jacek Szpaczek, Dribbble, ~224k visitas) — https://dribbble.com/shots/19182260--Medical-Queue-Management-Dashboard-Concept-Design — Lo que tomamos: la cola como superficie central con jerarquía de una pasada (qué sigue, quién está en atención), no como tabla administrativa secundaria.
3. **Ticket Dashboard** (Nurul Mustofa) — https://dribbble.com/shots/26558514-Ticket-Dashboard — Lo que tomamos: estados de turno como ciudadanía visual consistente (color + texto + punto) legibles sin leer.
4. **Help Desk Ticket Management SaaS Dashboard** (Panze UX Studio) — https://dribbble.com/shots/27024174-Help-Desk-Ticket-Management-SaaS-Dashboard-UI-UX-Design — Lo que tomamos: panel de agente orientado a "qué hago ahora", datos de analítica fuera de la cola viva.
5. **Dribbble — búsqueda "queue management"** — https://dribbble.com/search/queue-management — KRONOQ y afines (ticketing/KIOSK): patrón de número de turno grande y tipografía de display para la vista ciudadana.
6. **GOV.UK Service Manual** (principios de servicio público digital) — claridad, una tarea por pantalla, sin jerigonza.

## Bundle y calidad inicial (línea base "antes")

- `npm run build`: JS **272.36 kB** (86.05 kB gzip) en **un solo chunk** (sin code splitting por ruta), CSS **16.75 kB** (3.88 kB gzip). Build en ~3.2 s.
- Tests frontend: **0** archivos de test (`npm run test` → "No test files found", exit 1).
- Lint: `eslint . --max-warnings 0` configurado pero no ejecutado en CI; pendiente.
- `npx tsc --noEmit` → 0 errores (tras Fase previa de limpieza).

## Hallazgos priorizados

### CRÍTICO

| ID | Hallazgo | Evidencia |
|----|----------|-----------|
| C1 | **Navegación móvil inexistente**: `Navbar` oculta los enlaces en `hidden md:flex` sin alternativa (sin hamburger ni menú). Un administrador o recepcionista en móvil no puede cambiar de módulo. | `Navbar.tsx` líneas con `hidden md:flex` y sin `md:hidden` equivalente |
| C2 | **Asesor ve un error permanente al entrar**: `cargarTurno()` hace `GET /turnos/{user.usuarioId}` (usa el id de usuario como id de *turno*) → 404 → alerta "Recurso no encontrado" en cada carga. No existe endpoint "turno activo del asesor"; el fix es client-side con `GET /turnos` filtrando estado activo. | `AsesorPage.tsx` `cargarTurno`; confirmado en navegador (alerta roja al entrar) |
| C3 | **`ReportesPage` viola rules-of-hooks**: `if (!tieneRol(...)) return null;` antes de `useState`/`useEffect` (mismo patrón ya corregido en AdministradorPage). | Código actual |
| C4 | **Docs API desactualizados vs backend**: `tiempo-espera-promedio` documenta `{promedioMinutos, cantidad}` pero devuelve `{metrica, valor, cantidad_atenciones, fecha_desde, fecha_hasta}`. `ReportesPage`/`CoordinadorPage` funcionan leyendo `valor`/`cantidad_atenciones` (campos reales, no documentados). | `fetch` real: `{"metrica":"tiempo_espera_promedio_minutos","valor":0,"cantidad_atenciones":1,...}` vs `docs/api.md` |

### ALTO

| ID | Hallazgo | Evidencia |
|----|----------|-----------|
| A1 | Admin ve las 5 secciones en navbar con los mismos pesos visuales; falta jerarquía y estado activo (qué página estoy viendo). | `Navbar.tsx`, `tieneAlgunaRol` con override admin; sin `aria-current` |
| A2 | Estado de turno como texto plano/guiones; prioridad `PRIORITARIO` es un emoji crudo (`⭐`-like) que puede renderizar como `?` según codificación; no hay badge semántica consistente. | `RecepcionistaPage.tsx`/`CoordinadorPage.tsx` (`?` en celda) |
| A3 | **Ciudadano consulta: race** al escribir + clic en el mismo tick → "Turno no encontrado" espurio (segundo intento funcionó y mostró FINALIZADO). | Reproducción en navegador; el botón no espera el estado de React |
| A4 | **Doble llamada a `POST /atenciones/iniciar`** observada (2× PUT 200 separados por ~4 ms). Botón "Iniciar atención" sin guarda de transacción/disable por `loading` inmediato. | `atenciones/iniciar` ×2 en el panel de red |
| A5 | Tras **FINALIZADO**, el bloque "Finalizar atención" sigue visible con el botón activo → segundo clic → 409 al usuario. | Flujo real: turno FINALIZADO, botón "Finalizar atención" aún accionable |
| A6 | `CoordinadorPage`: `loadingState`/`setLoading` sin uso (Loading nunca se muestra); polling 5 s sustituye valores sin transición (parpadeo); sin distinción skeleton-vs-dato. | Código; `const [loadingState] = useState(false)` solo lectura |
| A7 | `index.html`: sin `<meta name="description">`, sin `theme-color`, sin favicon propio (icono Vite ausente → 404 silenciosa de `/vite.svg`). | `index.html` |

### MEDIO

| ID | Hallazgo | Evidencia |
|----|----------|-----------|
| M1 | Landing: rejillas `md:grid-cols-5` para "Beneficios" y "Roles" → tarjetas ~150 px con texto apretado en tablet; hero CTAs `flex-1` iguales en móvil sin prioridad; falta sección "Preguntas frecuentes" y explicación de qué es un turno para usuarios nuevos. | `LandingPage.tsx` |
| M2 | Imports mezclados (`@/components` barrel vs `@/components/Button` directo) en páginas. | múltiples páginas |
| M3 | **Duplicación por copiar/pegar**: patrón `useEffect → redirect por rol` replicado en 7 páginas (Admin, Asesor, Recepción, Coordinador, Ciudadano, Reportes, y lógica espejo en Navbar). Extraer `RequireRol`/`useRolGuard`. | Comparar páginas |
| M4 | `useAuth.ts` duplica `AuthContext.tsx` y **no se usa** en ningún sitio. | grep: solo la definición |
| M5 | `ValidationErrors` muestra icono roto (`⚠` en encoding sucio) y duplica el Alert que lo contiene; `Alert` con `fieldErrors` oculta el `mensaje` (doble caja roja anidada). | `ValidationErrors.tsx`, `Alert.tsx` |
| M6 | Login/registro: redirect con `window.location.href` (recarga completa); `username=data.correo` persistido → navbar muestra correo como "usuario". | `LoginPage.tsx`, `AuthContext` |
| M7 | Accesibilidad: faltan **skip link**; sin **live region** para cambios de estado del turno (feedback crítico para lector); tablas sin `scope="col"`; sin `scope`/roles en badges (texto truncado a simple `<span>` sin indicación de estado semántica); sin `autocomplete` (`email`, `current-password`); no se anuncia "turno generado". | Revisión de componentes |
| M8 | `EmptyState` desproporcionado (icono pequeño con `mt-8` excesivo) y con ítems de espaciado inconsistentes. | `EmptyState.tsx` |
| M9 | `App.tsx`: `setInicializado` inútil; mientras carga, rama `return null` sin indicador; `Navbar` depende de `user` y no hay skeleton. | `App.tsx` |
| M10 | CSS: tipografía del sistema heterogénea entre S.O. (`ui-sans-serif`); sin tokens (colores sueltos `blue-600`, `slate-*`; sin escala de radios/sombras propia; sin tokens de motion). | `index.css`, `tailwind.config.js` (`extend: {}`) |
| M11 | Tablas: sin estados hover/zebra, sin `scope`, sin fila vacía accesible (`EmptyState` inline), sin `aria-sort` donde aplique. | páginas Asesor/Recepción/Coordinador/Admin |

### BAJO

| ID | Hallazgo | Evidencia |
|----|----------|-----------|
| B1 | `Console` limpio excepto el 404 del asesor (C2) en cada navegación. | consola del navegador |
| B2 | `index.css` `font-family` distinto al resto (sistema vs Tailwind default). | `index.css` |
| B3 | Sin tests de frontend; sin CI de lint. | repo |
| B4 | `EmptyState`/`Button` sin variantes dark (decisión: sin dark mode por ahora; tokens quedarán listos). | — |
| B5 | `roleRoutes`/`Navbar` no indican página activa (`aria-current="page"`). | `Navbar.tsx` |
| B6 | Auth: token falso (`demo-token-…`) aceptable por diseño didáctico (RA6); no tocar. | — |
| B7 | `client.ts`/`GlobalExceptionHandler.java`: WIP previo del autor **no tocado y no mezclado** en este rediseño (ver git status). El backend devuelve stack traces completos a clientes — anotado; **fuera del alcance frontend** y del contrato acordado, se señala en el resumen. | working tree |

## Responsive (revisión de código, verificación final en Fase 6)

- `grid-cols-1` de base en casi todos los contenedores: apilan bien.
- `Navbar` sin alternativa móvil (C1) → bloqueante.
- Tablas con `overflow-x-auto` presente en Asesor/Recepción/Coordinador/Admin → OK; verificar que el scroll sea evidente y útil (sombra/indicador).
- CTAs full-width en landing móvil: bien; en `md+` hermanos estrechos (M1).
- Áreas táctiles: botones `sm` (~32 px) por debajo del mínimo recomendado (44 px) en Asesor/Recepción; `Ap`/`Button size=sm` y `Button variant="destructive" size="sm"` incumplirían el mínimo en táctil.

## Accesibilidad (para Fase 4B)

Contraste de blue-600/white y slate-500/white: AA OK. Foco visible presente vía `ring`. Faltan: skip link, `aria-current`, live regions, `scope`, `autocomplete`, badges con rol semántico, estados vacíos/loading con `role="status"`, `prefers-reduced-motion` (aún no hay animaciones, pero la Fase 4 lo exigirá).

## Plan priorizado (checklist)

**P0 — corregir antes de seguir (Fase 2/3):**
- [ ] C1 Menú móvil accesible (hamburger → sheet/menú, `aria-expanded`, foco).
- [ ] C2 Asesor: cargar datos sin 404 (usar `GET /turnos` y derivar; eliminar llamada errónea).
- [ ] C3 Reportes: mover hooks sobre el `return` condicional.
- [ ] C4 Documentar el contrato real en `docs/api.md` (o anotar discrepancia y alinear implementación con docs si el autor lo prefiere — decisión suya; el contrato no se toca en esta tarea).
- [ ] A4/A5 Guardas de transición y deshabilitar "Iniciar atención"/"Finalizar" durante/después de la acción.

**P1 — Sistema de diseño y pantallas (Fase 2–3):**
- [ ] Tokens: paleta institucional, tipografía Inter, spacing/radius/shadow/z-index, escala semántica de estados de turno con icono (punto + texto, GOV.UK style).
- [ ] BadgeEstado semántica con 7 estados + PRIORITARIO como badge aparte.
- [ ] M3/M2/M4 Refactor: `RequireRol`/`useRolGuard`, `useAuth.ts` eliminado, imports unificados (barrel `components/index.ts`).
- [ ] M5 `ValidationErrors` y `Alert` unificados (caja única, icono ⚠ válido, sin anidación doble).
- [ ] Landing refinada (P1 usuario: dashboards primero, landing después, sin scope creep).
- [ ] TODAS las páginas: jerarquía, espaciado, tablas, estados vacíos/error/carga coherentes (break-ui en Fase 3).

**P2 — Motion, responsive y a11y (Fase 4/4B):**
- [ ] Motion sutil funcional (estado de turno, listas, modales/toasts, transiciones de ruta (fade short)), `prefers-reduced-motion` respetado.
- [ ] Responsive verificado: Navbar móvil, tablas, CTA ciudadano a tamaño ciudadana.
- [ ] A4/A11Y: skip link, live region para "turno generado/llamado/finalizado", autocomplete, scope, badges con icono+texto.

**P3 — Calidad y entrega (Fase 5–7):**
- [ ] Code splitting por ruta + medición bundle antes/después; `vercel-react-best-practices` limpio; `code-review` final.
- [ ] Tests: login, registro, ProtectedRoute, formulario turno (recepcionista), BadgeEstado. Suite verde.
- [ ] Docs y CHANGELOG al día; rama `feature/frontend-redesign` pusheada; PR abierto.

## Riesgos aceptados

- Contrato de API `docs/design.md` no se modifica (instrucción del autor). Las discrepancias detectadas (C4) se documentan en este informe y en `docs/api.md` como corrección documental — aprobación implícita de que los docs reflejen la realidad implementada.
- `client.ts` y `GlobalExceptionHandler.java` (modificados por el autor antes de esta sesión) quedan **intactos** en el árbol de trabajo.
