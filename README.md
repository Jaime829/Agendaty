# Agendaty

Plataforma de gestión de turnos y atención al ciudadano. Backend Spring Boot 4.1.1 (Java 25) + frontend React + TypeScript + Vite, con API REST separada, seguridad JWT, reglas de negocio bajo concurrencia y reportes de tiempos de espera.

---

## Estructura del repositorio

```
/backend      → Spring Boot API REST (build propio, config, pruebas)
/frontend     → React + TypeScript + Vite (consume la API)
/docs         → documentación completa
docker-compose.yml → backend + frontend + base de datos
CHANGELOG.md  → registro de cambios
README.md     → este archivo
```

---

## Inicio rápido

### Backend

```powershell
cd backend
# (opcional) crear backend/.env con JWT_SECRET, CORS_ALLOWED_ORIGINS, DB_PASSWORD
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080/api/v1` y Swagger UI en `http://localhost:8080/swagger-ui.html`.

### Frontend

```powershell
cd frontend
cp .env.example .env
npm install
npm run dev   # http://localhost:5173
```

### Todo junto con Docker

```powershell
docker-compose up --build
```

---

## Pruebas

```powershell
# Backend (JUnit 5 + Mockito)
cd backend
./mvnw test

# Frontend (Vitest + Testing Library)
cd frontend
npm run test
```

---

## Documentación

Toda la documentación está en `/docs`:

| Documento | Descripción |
|-----------|-------------|
| `docs/README.md` | Índice de documentación |
| `docs/project-brief.md` | Especificación funcional y matriz de trazabilidad |
| `docs/architecture.md` | Arquitectura, diagramas, concurrencia RN1/RN4 |
| `docs/design.md` | Modelo de dominio, estados, API, UI/UX por rol |
| `docs/security.md` | JWT, permisos por rol, OWASP Top 10 |
| `docs/decisions.md` | Decisiones técnicas (ADR) |
| `docs/api.md` | Referencia de endpoints |
| `docs/setup.md` | Instalación, variables de entorno, Docker |
| `docs/testing.md` | Estrategia de pruebas y mapeo RN/CA → tests |
| `docs/frontend.md` | Estructura y convenciones del frontend |

---

## Reglas de negocio implementadas

| Regla | Descripción |
|-------|-------------|
| RN1 | Un turno no atendido por dos asesores al mismo tiempo |
| RN2 | Turno llamado sin respuesta pasa a "No atendido" |
| RN3 | Turnos prioritarios antes que normales (orden de llegada) |
| RN4 | Un asesor solo puede tener un turno activo |
| RN5 | Atención finalizada requiere resultado registrado |
| RN6 | Cálculo y persistencia de tiempos de espera y atención |

---

## Roles y permisos

`CIUDADANO`, `ASESOR`, `RECEPCIONISTA`, `COORDINADOR`, `ADMINISTRADOR` con matriz de permisos por endpoint (ver `docs/security.md`).

---

## Licencia

Privado — uso interno de la entidad.
