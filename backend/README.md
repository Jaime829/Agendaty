# Agendaty Backend

API REST de la plataforma de gestión de turnos y atención al ciudadano (Spring Boot 4.1.1 + Java 25).

## Estructura

```
backend/
├── pom.xml
├── mvnw / mvnw.cmd
├── .mvn/
├── src/
│   ├── main/java/com/proyecto/agendaty/  (controller, service, repository, entity, dto, mapper, exceptions, config, security, model.enums)
│   └── test/java/com/proyecto/agendaty/
└── target/
```

## Requisitos

- Java 25+
- Maven (se incluye wrapper `./mvnw`)
- Base de datos: H2 en memoria (dev/tests) o PostgreSQL/MySQL (prod, perfil `prod`)

## Variables de entorno

Crear `backend/.env` (no se versiona) o exportar antes de ejecutar:

| Variable | Descripción | Ejemplo / Valor por defecto |
|----------|-------------|-----------------------------|
| `JWT_SECRET` | Clave secreta para firmar JWT (base64) | `c2VjcmV0LW9uZGF0YXktY2xhdmUtZGVzYXJyYW5k...` |
| `JWT_EXPIRATION` | Expiración del token en ms | `86400000` (24h) |
| `DB_PASSWORD` | Password de la BD (prod) | — |
| `CORS_ALLOWED_ORIGINS` | Orígenes CORS permitidos | `http://localhost:5173` |
| `SONAR_TOKEN` | Token de SonarQube | — |

## Ejecución local (dev)

```powershell
cd backend
./mvnw spring-boot:run
```

La app expone:
- API REST: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Consola H2: `http://localhost:8080/h2-console`

## Pruebas

```powershell
cd backend
./mvnw test           # ejecuta JUnit 5 + Mockito
./mvnw verify         # build + tests + JaCoCo reporte
```

El reporte de cobertura JaCoCo se genera en `target/site/jacoco/`.

## Perfiles

- `default` / `dev`: H2 en memoria.
- `prod`: PostgreSQL/MySQL (configurar `spring.datasource.*` en `application-prod.properties`).
