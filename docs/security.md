# Security — Agendaty (simple)

## Autenticación
Login básico sin JWT. El usuario se autentica y su rol se almacena en localStorage (frontend). Los endpoints del backend están abiertos (sin @PreAuthorize) para simplicidad.

## CORS
`@CrossOrigin(origins = "http://localhost:5173")` en cada controller. Nunca `*` en producción.

## Matriz de permisos

| Acción | Ciudadano | Asesor | Recepcionista | Coordinador | Administrador |
|--------|-----------|--------|---------------|-------------|---------------|
| Generar turno | ❌ | ❌ | ✅ | ❌ | ✅ |
| Llamar siguiente | ❌ | ✅ | ❌ | ❌ | ❌ |
| Iniciar/pausar/finalizar atención | ❌ | ✅ | ❌ | ❌ | ❌ |
| Cancelar / no-atendido | ❌ | ❌ | ✅ | ❌ | ✅ |
| Ver reportes | ❌ | ❌ | ❌ | ✅ | ✅ |
| Gestionar usuarios/asesores/ventanillas | ❌ | ❌ | ❌ | ❌ | ✅ |

## Buenas prácticas
- Contraseñas con BCrypt (si se implementa login real).
- Secretos fuera del código (variables de entorno / `.env` no versionado).
- Validación de entrada con Bean Validation.
- No registrar datos sensibles en logs.
