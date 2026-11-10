# Agendaty Frontend

Aplicación cliente (React 18 + TypeScript + Vite) que consume la API REST del backend Agendaty.

## Estructura

```
frontend/
├── package.json
├── vite.config.ts
├── index.html
├── .env.example
├── public/
└── src/
    ├── api/           # cliente HTTP tipado
    ├── components/    # componentes reutilizables
    ├── hooks/         # hooks personalizados
    ├── pages/         # vistas por rol
    ├── routes/        # configuración de rutas
    ├── types/         # tipos TypeScript (DTOs del backend)
    ├── utils/         # utilidades
    └── main.tsx
```

## Requisitos

- Node.js 18+
- npm

## Instalación y ejecución

```powershell
cd frontend
cp .env.example .env   # editar VITE_API_URL si es necesario
npm install
npm run dev            # sirve en http://localhost:5173
```

## Pruebas

```powershell
cd frontend
npm run test           # Vitest + Testing Library
```

## Build de producción

```powershell
cd frontend
npm run build          # genera /dist
npm run preview        # sirve el build localmente
```

## Roles y vistas

| Rol | Ruta | Vista |
|-----|------|-------|
| Recepcionista | /recepcionista | Generar turno, cola, cancelar |
| Asesor | /asesor | Llamar siguiente, iniciar/pausar/finalizar atención |
| Coordinador | /coordinador | Monitor de cola, carga por asesor |
| Administrador | /administrador | Usuarios, roles, asesores, ventanillas, trámites |
| Ciudadano | /ciudadano | Consulta estado de turno |
