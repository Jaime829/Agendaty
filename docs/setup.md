# Setup — Agendaty

## Requisitos
- Java 25+
- Maven (wrapper incluido en `/backend/mvnw`)
- Node.js 18+ y npm
- Navegador web

## Backend (`/backend`)

```powershell
cd backend
./mvnw spring-boot:run
```

API en `http://localhost:8080`. Endpoints: `/turnos`, `/atenciones`, `/asesores`, `/ventanillas`, `/tipos-tramite`, `/reportes`, `/usuarios`.

Variables de entorno opcionales (`backend/.env`):
- `CORS_ALLOWED_ORIGINS=http://localhost:5173`

## Frontend (`/frontend`)

```powershell
cd frontend
cp .env.example .env
npm install
npm run dev
```

Frontend en `http://localhost:5173`.

Variable de entorno (`frontend/.env`):
- `VITE_API_URL=http://localhost:8080`

## Pruebas

Backend:
```powershell
cd backend
./mvnw test
```

Frontend:
```powershell
cd frontend
npm run test
```

## Build

Backend: `./mvnw package`
Frontend: `npm run build`
