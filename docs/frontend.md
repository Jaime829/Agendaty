# Documentación del Frontend — Agendaty

## Stack
- React 18 + TypeScript + Vite
- Tailwind CSS (estilo)
- React Router DOM (rutas)
- Axios (cliente HTTP)
- Lucide React (iconos)
- Vitest + Testing Library (pruebas)

## Estructura

```
frontend/
├── src/
│   ├── api/           # cliente HTTP tipado (auth, turnos, atenciones, asesores, admin, reportes)
│   ├── components/    # componentes reutilizables (Button, Input, Select, BadgeEstado, Loading, EmptyState, Alert, Navbar)
│   ├── hooks/         # useAuth
│   ├── pages/         # vistas por rol (Login, Registro, Recepcionista, Asesor, Coordinador, Administrador, Ciudadano, Reportes, Landing)
│   ├── context/       # AuthContext (sesión persistida en localStorage)
│   ├── routes/        # ProtectedRoute (rutas protegidas por rol)
│   ├── types/         # tipos TypeScript (DTOs del backend)
│   ├── utils/         # utilidades
│   └── assets/        # imágenes, íconos
├── .env.example       # VITE_API_URL, VITE_USE_MOCK, VITE_POLL_INTERVAL_MS, VITE_APP_TITLE
├── vite.config.ts
├── package.json
└── README.md
```

## Convenciones
- Tipos TypeScript en `src/types/index.ts` reflejan exactamente los DTOs del backend.
- URL de la API en variable de entorno `VITE_API_URL`.
- Modo controlado por `VITE_USE_MOCK=true` (datos en localStorage) / `false` (backend real).
- Cliente HTTP (`api/client.ts`) con interceptores para mapear errores de la API a mensajes claros.
- Rutas protegidas mediante `ProtectedRoute` que verifica rol antes de renderizar.
- `AuthContext` con sesión persistida, rutas protegidas por rol, redirección tras login.

## Vistas por rol

| Rol | Ruta | Vista principal |
|-----|------|-----------------|
| Ciudadano | `/ciudadano` | Consulta estado de turno por número |
| Recepcionista | `/recepcionista` | Generar turno (tipo de trámite, prioridad), lista de cola, cancelar |
| Asesor | `/asesor` | Llamar siguiente, iniciar/pausar/reanudar/finalizar atención con observaciones y resultado |
| Coordinador | `/coordinador` | Monitor de cola (polling 5s), carga por asesor, reporte tiempo espera |
| Administrador | `/administrador` | CRUD de usuarios internos, ventanillas, tipos de trámite |
| Reportes | `/reportes` | Tiempo promedio de espera con filtros de fecha |

## Calidad de UI/UX
- Estados de carga (`Loading`), vacío (`EmptyState`) y error (`Alert`).
- Validación de formularios alineada con Bean Validation del backend.
- Confirmación en acciones destructivas (cancelar turno).
- Responsive (Tailwind, móvil/tablet/escritorio).
- Accesibilidad: contraste, foco visible, etiquetas, HTML semántico.
- Microinteracciones: estados de carga en botones, transiciones sutiles.

## Ejecución

```powershell
cd frontend
npm install
npm run dev      # http://localhost:5173
npm run test     # Vitest
npm run build    # /dist
```
