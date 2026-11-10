# Documentación del Frontend — Agendaty

---

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
├── package.json
├── vite.config.ts
├── tsconfig.json / tsconfig.node.json
├── tailwind.config.js / postcss.config.js
├── index.html
├── .env.example
├── public/
└── src/
    ├── main.tsx          # punto de entrada
    ├── App.tsx           # rutas y autenticación
    ├── index.css         # Tailwind + base
    ├── vite-env.d.ts
    ├── api/
    │   ├── index.ts      # export de servicios
    │   ├── client.ts     # axios + interceptores (token + mapeo de errores)
    │   ├── auth.ts
    │   ├── turnos.ts
    │   ├── atenciones.ts
    │   ├── asesores.ts
    │   ├── tiposTramite.ts
    │   ├── reportes.ts
    │   └── usuarios.ts
    ├── components/
    │   ├── index.ts
    │   ├── Button.tsx
    │   ├── Input.tsx
    │   ├── Select.tsx
    │   ├── BadgeEstado.tsx
    │   ├── Loading.tsx
    │   ├── EmptyState.tsx
    │   ├── Alert.tsx
    │   └── Navbar.tsx
    ├── hooks/
    │   └── useAuth.ts
    ├── pages/
    │   ├── LoginPage.tsx
    │   ├── RecepcionistaPage.tsx
    │   ├── AsesorPage.tsx
    │   ├── CoordinadorPage.tsx
    │   ├── AdministradorPage.tsx
    │   ├── CiudadanoPage.tsx
    │   └── ReportesPage.tsx
    └── types/
        └── index.ts      # tipos TypeScript == DTOs del backend
```

## Convenciones

- Tipos TypeScript en `src/types/index.ts` reflejan exactamente los DTOs del backend (`Turno`, `Atencion`, `Asesor`, `TipoTramite`, `AuthUser`, `ApiError`).
- URL de la API en variable de entorno `VITE_API_URL`.
- El cliente HTTP (`api/client.ts`) inyecta el token JWT en cada petición y mapea errores de la API a mensajes claros (`ApiClientError` con `status`, `details`).
- Rutas protegidas mediante `PaginaProtegida` que redirige a `/login` si no hay usuario autenticado.
- Menú y vistas filtradas por rol (matriz de permisos en `docs/security.md`).

## Vistas por rol

| Rol | Ruta | Vista principal |
|-----|------|-----------------|
| Recepcionista | `/recepcionista` | Generar turno (seleccionar trámite, prioridad), lista de cola, cancelar turno |
| Asesor | `/asesor` | Llamar siguiente, iniciar/pausar/reanudar/finalizar atención con observaciones y resultado |
| Coordinador | `/coordinador` | Monitor de cola en tiempo casi real (polling 5s), carga por asesor, reporte tiempo espera |
| Administrador | `/administrador` | Gestión de usuarios, roles, módulos |
| Ciudadano | `/ciudadano` | Consulta de estado de turno por número |
| Reportes | `/reportes` | Tiempo promedio de espera con filtros de fecha |

## Calidad de UI/UX

- Estados de carga (`Loading`), vacío (`EmptyState`) y error (`Alert`).
- Validación de formularios alineada con Bean Validation del backend.
- Confirmación en acciones destructivas (cancelar turno).
- Responsive (Tailwind, móvil/tablet/escritorio).
- Accesibilidad: foco visible, etiquetas en inputs, HTML semántico, contraste.
- Microinteracciones: estados de carga en botones, transiciones sutiles.

## Ejecución

```powershell
cd frontend
npm install
npm run dev      # http://localhost:5173
npm run test     # Vitest
npm run build    # /dist
```
