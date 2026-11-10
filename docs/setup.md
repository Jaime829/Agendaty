# Instalación y ejecución — Agendaty

Este documento describe cómo instalar, ejecutar, probar y desplegar el backend y el frontend por separado, y con Docker.

---

## Requisitos generales

| Componente | Versión |
|------------|---------|
| Java | 25+ |
| Maven | 3.9+ (wrapper incluido) |
| Node.js | 18+ |
| npm | 10+ |
| Docker / Docker Compose | para despliegue completo |

---

## Backend (`/backend`)

### Variables de entorno

Crear `backend/.env` (no versionado) o exportar antes de ejecutar:

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `JWT_SECRET` | `c2VjcmV0LXdvbmFkYXRheS1jbGF2ZS1kZXMtZGVzYXJy...` | Clave secreta JWT (base64) |
| `JWT_EXPIRATION` | `86400000` | Expiración del token en ms (24h) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Orígenes CORS permitidos (coma-separados) |
| `DB_PASSWORD` | — | Password de BD (perfil `prod`) |
| `SPRING_PROFILES_ACTIVE` | `default` | `default` (H2) o `prod` (PostgreSQL/MySQL) |

### Ejecución local (dev, H2 en memoria)

```powershell
cd backend
./mvnw spring-boot:run
```

Endpoints:
- API REST: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Consola H2: `http://localhost:8080/h2-console`

### Pruebas y build

```powershell
cd backend
./mvnw test          # JUnit 5 + Mockito (46 tests)
./mvnw verify        # build + tests + reporte JaCoCo
```

El reporte de cobertura se genera en `target/site/jacoco/`.

### Perfiles

- `default` / `dev`: H2 en memoria, sin servidor externo.
- `prod`: PostgreSQL/MySQL. Configurar `spring.datasource.url/username/password` en `application-prod.properties` o variables de entorno.

---

## Frontend (`/frontend`)

### Variables de entorno

Copiar `frontend/.env.example` a `frontend/.env` y editar:

| Variable | Valor ejemplo | Descripción |
|----------|---------------|-------------|
| `VITE_API_URL` | `http://localhost:8080/api/v1` | URL del backend |
| `VITE_POLL_INTERVAL_MS` | `5000` | Intervalo de polling del coordinador |
| `VITE_APP_TITLE` | `Agendaty - Atención al Ciudadano` | Título de la app |

### Instalación y ejecución

```powershell
cd frontend
cp .env.example .env
npm install
npm run dev          # sirve en http://localhost:5173
```

### Pruebas y build

```powershell
cd frontend
npm run test         # Vitest + Testing Library
npm run build        # genera /dist (producción)
npm run preview      # sirve el build localmente
```

---

## Despliegue con Docker Compose

Desde la raíz del repositorio:

```powershell
docker-compose up --build
```

Levanta:
- `db` (PostgreSQL 16) en puerto 5432
- `backend` (Spring Boot) en puerto 8080 (perfil `prod`)
- `frontend` (nginx) en puerto 5173

Variables necesarias en entorno antes de levantar:
- `JWT_SECRET`
- `DB_PASSWORD`
- `CORS_ALLOWED_ORIGINS` (ej. `http://localhost:5173`)

### Archivos de soporte

- `backend/Dockerfile` — build multietapa del backend
- `frontend/Dockerfile` — build del frontend + nginx
- `frontend/nginx.conf` — proxy de `/api` al backend y SPA fallback
