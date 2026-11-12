# Security — Agendaty (esquema didáctico simple)

## Autenticación (RA1, RA2)
- Registro público: `POST /auth/registro`. Cualquier persona puede registrarse. Rol por defecto SIEMPRE `CIUDADANO`. El backend ignora cualquier rol enviado.
- Login: `POST /auth/login` con correo + contraseña. Si es correcto, devuelve `{id, nombre, correo, rol}`.
- El frontend guarda la sesión (AuthContext + localStorage) y redirige según el rol.
- Contraseñas guardadas con BCrypt (solo `spring-security-crypto`, sin activar Spring Security completo - RA5).
- Las contraseñas nunca se devuelven en las respuestas.

## Autorización (RA6)
- Cada petición protegida envía el header `X-Usuario-Id`.
- El service busca el usuario por id y valida que su rol tenga permiso para la acción.
- Si no tiene permiso, devuelve 403.
- ⚠️ Esquema didáctico. En producción se usaría JWT con Spring Security.

## CORS
`@CrossOrigin(origins = "http://localhost:5173")` en cada controller. Nunca `*` en producción.

## Matriz de permisos (RA7)

| Acción | Ciudadano | Asesor | Recepcionista | Coordinador | Administrador |
|--------|-----------|--------|---------------|-------------|---------------|
| Registrarse (público) | ✅ | ❌ | ❌ | ❌ | ❌ |
| Login (público) | ✅ | ✅ | ✅ | ✅ | ✅ |
| Generar turno | ❌ | ❌ | ✅ | ❌ | ✅ |
| Llamar siguiente turno | ❌ | ✅ | ❌ | ❌ | ❌ |
| Iniciar atención | ❌ | ✅ | ❌ | ❌ | ❌ |
| Pausar atención | ❌ | ✅ | ❌ | ❌ | ❌ |
| Reanudar atención | ❌ | ✅ | ❌ | ❌ | ❌ |
| Finalizar atención | ❌ | ✅ | ❌ | ❌ | ❌ |
| Cancelar turno | ❌ | ❌ | ✅ | ❌ | ✅ |
| Marcar no atendido | ❌ | ❌ | ✅ | ❌ | ✅ |
| Cambiar disponibilidad asesor | ❌ | ❌ | ❌ | ❌ | ✅ |
| Ver reportes | ❌ | ❌ | ❌ | ✅ | ✅ |
| Registrar usuarios internos | ❌ | ❌ | ❌ | ❌ | ✅ |
| Gestionar ventanillas | ❌ | ❌ | ❌ | ❌ | ✅ |
| Gestionar tipos de trámite | ❌ | ❌ | ❌ | ❌ | ✅ |
| Consultar estado de turno propio | ✅ | ❌ | ❌ | ❌ | ❌ |

## Validación de entrada
- Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Pattern`, etc.) en DTOs de entrada.
- Validación en el service para reglas de negocio.

## Secretos y variables de entorno
- `ADMIN_EMAIL` y `ADMIN_PASSWORD`: credenciales del admin seed (solo desarrollo).
- `CORS_ALLOWED_ORIGINS`: orígenes permitidos (por defecto `http://localhost:5173`).
- Nunca se guardan credenciales en el código.

## Logs
- No se registran contraseñas ni datos sensibles en los logs.
