# Seguridad — Agendaty

> Contrato de seguridad. Se redacta ANTES de terminar el backend y se mantiene sincronizado con el código. Basado en el skill `springboot-security` y OWASP Top 10.

---

## 1. Modelo de autenticación y autorización

El proyecto **no tiene seguridad implementada** en la línea de base (Fase 1). Se propone e implementa **JWT (JSON Web Token) con Spring Security**, justificado porque:

- Es stateless, adecuado para una arquitectura backend/frontend separada (Fase 5).
- No requiere sesiones en servidor, escalable.
- Estándar de industria para APIs REST consumidas por SPA.

**Flujo de autenticación:**
1. El cliente envía credenciales a `POST /api/v1/auth/login`.
2. Spring Security valida contra `UserDetailsService` (usuarios almacenados en BD con roles).
3. Si son válidas, se genera un JWT firmado con clave secreta (variable de entorno `JWT_SECRET`), conteniendo: `sub` (username), `roles`, `exp`, `iat`.
4. El cliente envía el JWT en el header `Authorization: Bearer <token>` en cada petición.
5. Un filtro `JwtAuthenticationFilter` valida el token, establece la autenticación en el `SecurityContext` y Spring autoriza por rol.

**Perfiles:** en desarrollo (`dev`) se puede permitir acceso más relajado para pruebas; en producción (`prod`) todos los endpoints excepto `/api/v1/auth/**` requieren autenticación.

## 2. Matriz de permisos por rol × endpoint/acción

| Endpoint | CIUDADANO | ASESOR | RECEPCIONISTA | COORDINADOR | ADMINISTRADOR |
|----------|-----------|--------|---------------|-------------|---------------|
| POST `/api/v1/auth/login` | ✅ | ✅ | ✅ | ✅ | ✅ |
| POST `/api/v1/auth/register` | ✅ | ❌ | ❌ | ❌ | ✅ |
| GET `/api/v1/tipos-tramite` | ✅ | ✅ | ✅ | ✅ | ✅ |
| POST/PUT/DELETE `/api/v1/tipos-tramite` | ❌ | ❌ | ❌ | ❌ | ✅ |
| POST `/api/v1/turnos` | ❌ | ❌ | ✅ | ❌ | ✅ |
| GET `/api/v1/turnos` | ❌ (solo propios) | ✅ | ✅ | ✅ | ✅ |
| PUT `/api/v1/turnos/{id}/llamar` | ❌ | ✅ | ❌ | ❌ | ❌ |
| PUT `/api/v1/turnos/{id}/cancelar` | ❌ | ❌ | ✅ | ❌ | ✅ |
| PUT `/api/v1/turnos/{id}/no-atendido` | ❌ | ❌ | ✅ | ❌ | ✅ |
| PUT `/api/v1/atenciones/iniciar` | ❌ | ✅ | ❌ | ❌ | ❌ |
| PUT `/api/v1/atenciones/{id}/pausar\|reanudar\|finalizar` | ❌ | ✅ | ❌ | ❌ | ❌ |
| GET `/api/v1/asesores` | ❌ | ✅ | ✅ | ✅ | ✅ |
| GET/PUT/DELETE `/api/v1/ventanillas` | ❌ | ✅ (lectura) | ✅ (lectura) | ✅ | ✅ |
| GET `/api/v1/reportes/*` | ❌ | ❌ | ❌ | ✅ | ✅ |
| GET/POST/PUT `/api/v1/usuarios` | ❌ | ❌ | ❌ | ❌ | ✅ |

## 3. Seguridad de contraseñas y gestión de secretos

- Contraseñas almacenadas con **BCrypt** (`passwordEncoder` de Spring Security), nunca en texto plano.
- Secretos **nunca en el código**: `JWT_SECRET`, `DB_PASSWORD`, claves de API se inyectan mediante variables de entorno y `.env` (no versionado).
- `application-*.yml` usa placeholders: `${JWT_SECRET}`, `${DB_PASSWORD}`, etc.
- JWT con expiración corta (configurable, ej. 12h) y opcionalmente refresh token.

## 4. CORS y CSRF

- **CORS:** configurado explícitamente con orígenes permitidos desde variable de entorno (`CORS_ALLOWED_ORIGINS`, ej. `http://localhost:5173` para dev, dominio del frontend en prod). **Nunca `*` en producción.**
- **CSRF:** deshabilitado para la API REST (stateless JWT); se protege mediante validación del token Bearer y, en futuro, SameSite cookies si se usara sesión.

## 5. Validación de entradas y protección contra inyecciones

- Bean Validation (`jakarta.validation`) en todos los DTOs de entrada (`@NotBlank`, `@Size`, `@Pattern`, `@Email`, etc.).
- Spring Data JPA usa consultas parametrizadas → protegido contra **inyección SQL**.
- springdoc/OpenAPI expone solo endpoints definidos; sin exposición accidental de entidades.
- Sanitización de datos de salida para evitar **XSS** (el frontend escapa contenido renderizado; el backend no devuelve HTML).

## 6. Rate limiting y protección de endpoints públicos

- Endpoints públicos (`/api/v1/auth/login`, `/api/v1/auth/register`) protegidos contra fuerza bruta con rate limiting (configurable, ej. 5 intentos por minuto por IP).
- Se recomienda `spring-boot-starter-rate-limit` o Bucket4j (decisión en `decisions.md`).

## 7. Auditoría y trazabilidad

- Cada acción sobre turnos (generar, llamar, iniciar, pausar, finalizar, cancelar, no-atendido) se registra en una entidad `Auditoria` con: `usuarioId`, `usuarioNombre`, `accion`, `entidad`, `entidadId`, `fecha`, `direccionIP`.
- Los reportes y consultas de turno por ciudadano están sujetos a autorización (no se expone información de terceros).
- Logs de aplicación **sin información sensible** (no se logean contraseñas, tokens completos, ni datos personales sensibles del ciudadano).

## 8. Datos personales del ciudadano

- Solo el ciudadano propio, recepcionista, asesor asignado, coordinador y administrador pueden ver los datos del ciudadano vinculados a un turno.
- `documento` y `telefono` se tratan como datos sensibles; se minimiza su exposición en listados.

## 9. Checklist OWASP Top 10 aplicado al proyecto

| OWASP Top 10 | Estado | Acción en el proyecto |
|--------------|--------|-----------------------|
| A01:2021 – Broken Access Control | 🔲 Pendiente | Autorización por rol en cada endpoint (Spring Security `@PreAuthorize`) |
| A02:2021 – Cryptographic Failures | 🔲 Pendiente | Contraseñas BCrypt, JWT firmado, HTTPS en producción |
| A03:2021 – Injection | 🔲 OK | Spring Data JPA parametrizado, Bean Validation |
| A04:2021 – Insecure Design | 🔲 Pendiente | security.md como contrato, validación en capa de servicio |
| A05:2021 – Security Misconfiguration | 🔲 Pendiente | CORS explícito, secretos por variable de entorno, perfiles |
| A06:2021 – Vulnerable Components | 🔲 Pendiente | Dependencias gestionadas por Spring Boot BOM, revisar CVEs |
| A07:2021 – Identification Failures | 🔲 Pendiente | JWT con expiración, sessiones stateless |
| A08:2021 – Software Data Failures | 🔲 Pendiente | Validación de entrada, serialización controlada (DTOs) |
| A09:2021 – Logging Failures | 🔲 Pendiente | Auditoría de acciones, logs sin datos sensibles |
| A10:2021 – SSRF | 🔲 Pendiente | No hay consumo de URLs externas definido; revisar si se añade |
