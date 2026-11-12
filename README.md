# Agendaty

Plataforma de gestión de turnos y atención al ciudadano. Backend Spring Boot 4.1.1 (Java 25) + frontend React + TypeScript + Vite, con API REST separada, autenticación simple, reglas de negocio bajo concurrencia y reportes de tiempos de espera.

---

## Estructura del repositorio

```
/backend      → Spring Boot API REST (build propio, config, pruebas)
/frontend     → React + TypeScript + Vite (consume la API)
/docs         → documentación completa
CHANGELOG.md  → registro de cambios
README.md     → este archivo
```

---

## Inicio rápido

### Backend

```powershell
cd backend
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080` y Swagger UI en `http://localhost:8080/swagger-ui.html`.

Credenciales de administrador (seed automático):
- Correo: `admin@turnos.local`
- Contraseña: `Admin123*`

### Frontend

```powershell
cd frontend
cp .env.example .env   # editar VITE_API_URL=http://localhost:8080 si es necesario
npm install
npm run dev            # http://localhost:5173
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

Resultado: **44 tests backend, 0 fallos, 100% verde**.

---

## Documentación

Toda la documentación está en `/docs`:

| Documento | Descripción |
|-----------|-------------|
| `docs/README.md` | Índice de documentación |
| `docs/project-brief.md` | Especificación funcional y matriz de trazabilidad |
| `docs/architecture.md` | Arquitectura, diagramas, concurrencia RN1/RN4 |
| `docs/design.md` | Modelo de dominio, estados, API, UI/UX por rol |
| `docs/security.md` | JWT-less, permisos por rol, OWASP Top 10 |
| `docs/decisions.md` | Decisiones técnicas (ADR) |
| `docs/api.md` | Referencia de endpoints |
| `docs/setup.md` | Instalación, variables de entorno, Docker |
| `docs/testing.md` | Estrategia de pruebas y mapeo RN/CA/RA → tests |
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

### Requisitos adicionales (RA)

- RA1: Registro público con rol CIUDADANO forzado
- RA2: Login con correo + contraseña
- RA3: Admin sembrado automáticamente (CommandLineRunner)
- RA4: Admin registra usuarios internos (POST /admin/usuarios)
- RA5: Contraseñas con BCrypt
- RA6: Autorización por rol con header X-Usuario-Id
- RA7: Matriz de permisos rol × acción

---

## Roles y permisos

`CIUDADANO`, `ASESOR`, `RECEPCIONISTA`, `COORDINADOR`, `ADMINISTRADOR` con matriz de permisos por endpoint (ver `docs/security.md`).

---

## Licencia

Privado — uso interno de la entidad.
