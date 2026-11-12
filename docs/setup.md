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

API en `http://localhost:8080`. Endpoints: `/turnos`, `/atenciones`, `/asesores`, `/ventanillas`, `/tipos-tramite`, `/reportes`, `/usuarios`, `/auth/registro`, `/auth/login`, `/admin/usuarios`.

Variables de entorno (`.env` o exportar antes de ejecutar):
- `ADMIN_EMAIL`: correo del administrador semilla (default: `admin@turnos.local`)
- `ADMIN_PASSWORD`: contraseña del administrador (default: `Admin123*`)
- `CORS_ALLOWED_ORIGINS`: orígenes permitidos (default: `http://localhost:5173`)

⚠️ Las credenciales por defecto son SOLO para desarrollo. Cámbielas en producción.

## Frontend (`/frontend`)

```powershell
cd frontend
cp .env.example .env
npm install
npm run dev
```

Frontend en `http://localhost:5173`.

Variables de entorno (`frontend/.env`):
- `VITE_API_URL=http://localhost:8080`
- `VITE_USE_MOCK=true` (usa datos simulados sin backend) / `false` (usa backend real)
- `VITE_POLL_INTERVAL_MS=5000`
- `VITE_APP_TITLE=Agendaty - Atención al Ciudadano`

## Credenciales de desarrollo (seed automático)

Al arrancar el backend, se crea automáticamente un administrador si no existe:

| Campo | Valor por defecto |
|-------|-------------------|
| Correo | `admin@turnos.local` |
| Contraseña | `Admin123*` |
| Rol | `ADMINISTRADOR` |

Usuarios de ejemplo en modo mock:
| Correo | Contraseña | Rol |
|--------|------------|-----|
| `admin@turnos.local` | `Admin123*` | ADMINISTRADOR |
| `recepcion@turnos.local` | `Rec123*` | RECEPCIONISTA |
| `asesor@turnos.local` | `Ase123*` | ASESOR |
| `coord@turnos.local` | `Cor123*` | COORDINADOR |
| `juan@email.com` | `Cli123*` | CIUDADANO |

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

## Despliegue con Docker Compose

Desde la raíz del repositorio:

```powershell
docker-compose up --build
```

Levanta: `db` (PostgreSQL 16), `backend` (Spring Boot, puerto 8080), `frontend` (nginx, puerto 5173).
