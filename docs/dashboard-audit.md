# Auditoría de Dashboard — Administración (Agendaty)

Fecha: 2026-11-15 · Rama: `feature/dashboard-redesign` · Antecedente: `docs/frontend-audit.md` (rediseño global ya aplicado) · Estado: línea base para el rediseño de dashboards (Admin → Coordinador → Asesor → Recepción → Ciudadano → Reportes).

## Entorno verificado (real)

Frontend `http://localhost:5173` (Vite, `VITE_USE_MOCK=false`, modo real con backend `http://localhost:8080`). Sesión `admin@turnos.local`. Altura de viewport de prueba 670 px. Consola **sin errores** en `/administrador`. Registros en H2 al momento: admin + recepcion + coord + asesor + juan (5 usuarios).

Base: `GET /admin/usuarios` (lista completa ≥ 200), `GET /asesores`, `GET /ventanillas`, `GET /tipos-tramite`, `GET /turnos`, `GET /reportes/*` — todos existen en el backend y hoy los devuelve para ADMIN. **Cero endpoints nuevos**; los KPIs se calculan en cliente o se consumen tal cual.

## Métricas actuales (antes)

- El dashboard de Administración **no es un dashboard**: no hay KPIs ni visión de conjunto; contiene un formulario + una tabla.
- Formulario «Crear usuario» ocupa toda la zona superior (empieza en y≈190; título «Usuarios del sistema») y la **tabla de usuarios queda fuera del viewport inicial** (scroll inicial 1331 px).
- Panel «Módulos» (botones de navegación) empieza en y≈994 duplicando la navbar.
- Navbar (admin): 6 enlaces de sección + correo + badge + Salir (saturación visual, sin agrupar).
- Formulario: etiquetas sin leyenda de obligatorio, contraseña sin ver/ocultar ni requisitos, `<select>` nativo, ancho completo, botón primario muy pesado, campos inconsistentes.
- Tabla de usuarios: sin búsqueda, sin filtro por rol, sin orden, sin paginación, sin acciones por fila, badges de rol indiferenciados, cabecera uppercase apenas distinta.

## Hallazgos priorizados

### Crítico
C1. **No hay KpiCard ni resumen del estado del sistema** (0 métricas visibles). Un administrador no sabe cuántos usuarios hay por rol, cuántos asesores disponibles ni cuántas ventanillas/trámites tiene sin buscar en los controles.
C2. **El formulario de alta empuja lo consultable fuera de pantalla**: se prioriza el caso menos frecuente (crear usuario) sobre el más frecuente (consultar/gestionar usuarios).
C3. **La gestión de Ventanillas y Tipos de trámite NO existe en el frontend**: el backend las soporta completamente (`POST/GET/PUT/DELETE /ventanillas`, `POST/GET/PUT/DELETE /tipos-tramite`) y la navbar/el contrato las exigen al admin (RA4), pero la pantalla no las ofrece. Hoy el admin no puede hacer su trabajo completo desde la UI.

### Alto
A1. **Panel «Módulos» redundante**: repite los enlaces de la navbar con botones grandes; cero valor, empuje vertical innecesario. Se elimina y, si hace falta, se reemplaza por una acción contextual (p. ej. «Ver cola en vivo»).
A2. **Navbar para admin saturada y plana** (6 enlaces al mismo nivel + correo + rol + Salir). Agrupar y limpiar: actualmente activo claro + menú de usuario desplegable (correo, rol, Salir).
A3. **Formulario de alta mejorable en UX/accesibilidad**: sin leyenda de obligatorio, contraseña sin ver/ocultar ni requisitos, `<select>` nativo, campos inconsistentes (unos con placeholder, otros no), botón full-width y muy grande, rol sin icono/ayuda.
A4. **Tabla de usuarios sin capacidades básicas**: sin buscar, sin filtrar por rol, sin orden, sin paginación, sin acciones por fila (editar/activar/restablecer según contrato — hoy el contrato solo permite crear y listar; NO editar/desactivar/restablecer contraseña; por tanto, las acciones de fila que el contrato no soporta se **no** se muestran).
A5. **Badges de rol indiferenciados**: todos igual; no se distinguen roles entre sí ni de estados de turno.

### Medio
M1. Jerarquía tipográfica: título de sección del mismo peso/escala; no hay sub-KPIs ni etiquetas de contexto; tipografía global correcta pero sin escala de producto.
M2. Contraste: encabezados OK; las celdas de tabla en slate-600 aceptable; detalles (dots de estado) — verificar AA en todos los combos badge; muted slate-500 sobre blanco ≈ 5.3 (OK pero justo en xs).
M3. Estados: skeleton inline básico (repite el patrón, pero incompleto frente a errores; sin skeletons de KPI; sin `EmptyState`/`ErrorState` con retry en listas; sin toast para éxito de alta (hay toast sonner pero sin confirmación de creación visible fuera de la tabla).
M4. Responsive móvil: la rejilla `xl:grid-cols-3` aprieta el formulario en móviles; la tabla hace `overflow-x` bien pero no colapsa en tarjetas; sin verificación formal a 375 px.
M5. Teclado/foco: foco visible global bien, pero el orden de tabulación del formulario no está revisado; el panel Módulos añadía ruido de tab-order; nav móvil usa `aria-expanded`/`aria-controls` ✓; el `select` nativo es accesible pero visualmente pobre — reemplazar por menú accesible propio.
M6. Consistencia con otros dashboards: Coordinador (funciona, sin KPIs explícitos ni refresco visible), Asesor (OK tras rediseño, con atajos), Recepción (OK tras rediseño, sin lista en vivo persistente), Ciudadano (OK tras rediseño). Familia visual coherente desde el rediseño, pero sin piezas compartidas (KpiCard, DataTable, StatusBadge, PageHeader, Drawer/Modal formal).
M7. Densidad: densidad media; pedir densidad compacta para operación (ventanilla) sin sacrificar legibilidad; paddings internos de tarjetas grande; cabeceras de tabla normales (no xs).

### Bajo
B1. Sin «tooltip»/ajuste de etiqueta sobre acciones (editar/desactivar deshabilitadas sin explicación).
B2. Sin favicon SVG propio mono; icono Building2 de Lucide reutilizado en navbar/landing (coherente, pero cabe variar con marca).
B3. Consola limpia; sin warnings de React (tras rediseño previo).
B4. `document.title` estático en todas las rutas; sin `aria-live` declarado en cambios de estado de turno dentro de la cola (solo KPI de Coordinador lo usa).

## Plan priorizado (checklist)

**P0 — Admin de verdad (Fase 3):**
- [ ] C1 KPIs (5): Usuarios totales, Asesores disponibles, Ventanillas, Tipos de trámite, Turnos hoy · skeleton mientras carga.
- [ ] C2 Tabla protagonista (búsqueda por nombre/correo/documento, filtro por rol, orden por columnas, paginación 10/página, contador «mostrando X de Y») · acciones por fila **solo si el contrato lo soporta** (hoy: ninguna → se omiten y se documenta).
- [ ] C3 Secciones habilitadas por contrato: Usuarios | Ventanillas (crear/editar/eliminar con confirmación) | Tipos de trámite (crear/editar/eliminar) — sin CRUD de usuarios internos fuera del contrato (no PATCH/DELETE `/admin/usuarios`).
- [ ] A1 Eliminar «Módulos»; reemplazar por enlace contextual «Ver cola en vivo» a `/coordinador` (atajo real; abre la vista de cola).
- [ ] A2 Navbar limpia: activos claros; menú de usuario desplegable (correo, rol, Salir); nav móvil igual que ahora (hamburguesa ✓) pero agrupada por secciones.
- [ ] A3 Drawer/modal «Nuevo usuario»: formulario 2 columnas, leyendas, ayudas bajo campos, ver/ocultar contraseña con requisitos visibles, `role` accesible propio, botón tamaño normal con estado de carga.
- [ ] A5 Badges de rol diferenciados (color + texto).
- [ ] M2 Verificación de contraste AA en badges nuevos.

**P1 — Extender a todos (Fase 4):**
- [ ] Componentes compartidos: `KpiCard`, `DataTable`, `StatusBadge`, `PageHeader`, `Drawer`/`Modal`, `EmptyState`, `ErrorState`, `Skeleton` — documentar en `turnos-dashboard-patterns` (skill en Fase 2–3).
- [ ] Coordinador: monitor con KPIs (en espera/en atención/en pausa/espera promedio) + tabla en vivo sin parpadeos (update silencioso).
- [ ] Asesor: acción «Llamar siguiente» dominante, turno actual grande, cronómetro de atención, resultado obligatorio.
- [ ] Recepción: generar turno rápido + cola en vivo persistente (polling 5 s igual que Coordinador).
- [ ] Ciudadano: estado del turno en 2 s desde celular (número grande + estado + prioridad).
- [ ] Reportes: barras claras, filtro de fechas, tablas legibles.

**P2 — Cierre (Fases 5–7):**
- [ ] Motion con propósito (entrada de KPIs, apertura drawer, cambio de fila de cola, toasts) + `prefers-reduced-motion` respetado.
- [ ] Responsive real (375/768/1280) y teclado completo + break-ui de cada pantalla.
- [ ] `npm run build`, lint, tests verdes; bundle comparado.
- [ ] Docs (`frontend.md`, `decisions.md`, `CHANGELOG.md`) y push de `feature/dashboard-redesign` con PR.

## Referencias visuales (Fase 2 — inspiración, nunca copia)

1. **Medical Queue Management Dashboard** (Jacek Szpaczek, Dribbble, ~224k vistas) — https://dribbble.com/shots/19182260--Medical-Queue-Management-Dashboard-Concept-Design — la cola viva como superficie primaria con jerarquía de una pasada; tabla ordenable por prioridad y estado; cabecera clara.
2. **KRONOQ — Modern SaaS Queue Management** (Deswork, Dribbble) — https://dribbble.com/search/queue-management — bloques de KPI operativos arriba (línea base de contadores y estados) y lista debajo; patrón directo de «fila de KPIs + tabla protagonista» para nuestro Admin; bloques grandes legibles.
3. **GOV.UK Design System — Patterns** — https://design-system.service.gov.uk/patterns/ — formularios con etiqueta visible, bloques de error (error summary) y tags de estado; principios: una tarea por pantalla, errores directos, etiquetas siempre visibles. Marco conceptual que ya aplica al resto del producto.
4. **Queue Dashboard — Genesys Cloud CX documentation** — https://all.docs.genesys.com/PEC-REP/Current/RPRT/HRCXIQueueDshbrd — KPIs por cola en pestañas (KPIs + Daily Summary) y filtros por vista; filtramos solo lo necesario por pantalla, sin toolbars infinitos.
5. **Ticket Dashboard** (Nurul Mustofa, Dribbble) — https://dribbble.com/shots/26558514-Ticket-Dashboard — una fila por registro con estados en chip de color+texto; jerarquía compacta, densa y clara; nada de decoración superflua.

## Dirección aprobada (Fase 2, decisiones del autor)

- **GOV.UK institucional**: sobrio, directo, con tablas densas, formularios precisos y errores en español claro; sin adornos de marketing dentro del producto.
- **Densidad compacta** para operación (ventanilla y monitor).
- **Marca azul `#175b9c` intacta**; blanco y neutrales frescos; semánticos de estado ya definidos (tokens `estado-*`).
- **Dark mode fuera de alcance** esta iteración (Fase 2-7 del rediseño de dashboards); tokens listos para añadirlo.
- **Acciones por fila omitidas** (el contrato REST no las soporta: solo crear/listar usuarios).
- **Secciones habilitadas por contrato**: Usuarios · Ventanillas (CRUD) · Tipos de trámite (CRUD) — backed by `VentanillaController` y `TipoTramiteController`.
- **KPI aprobado**: Usuarios totales con desglose por rol (único conjunto pedido). KPIs de capacidad (asesores/ventanillas/trámites) NO fueron seleccionados por el autor; se listan como extras opcionales en `docs/decisions.md`.

## Datos para KPIs — diccionario (fuente por cada KPI)

| KPI | Endpoint/derivación | Nota |
|---|---|---|
| Usuarios totales (y por rol) | `GET /admin/usuarios` | count + groupBy rol en cliente |
| Asesores disponibles | `GET /asesores` | count `disponible` |
| Ventanillas | `GET /ventanillas` | total y ocupadas (asesorId no nulo) |
| Tipos de trámite | `GET /tipos-tramite` | count `activo` |
| Turnos hoy | `GET /turnos` | filter `fechaCreacion` fecha de hoy (cliente); si no existe el dato → mostrar como «hoy» con aviso |
| En espera / en atención | `GET /turnos` | filter estado (Coordinador ya lo hace) |
| Espera promedio | `GET /reportes/tiempo-espera-promedio` | actual (ya usado por Coordinador/Reportes) |
