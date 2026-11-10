# Changelog — Agendaty

## [1.0.0] — Versión completa (2026-11-10)

### ✨ Nuevas funcionalidades

- **Gestión de turnos**: generación de turnos con número único, tipo de trámite y prioridad (normal/prioritario), estados completos (En espera, Llamado, En atención, En pausa, Finalizado, Cancelado, No atendido).
- **Llamado de siguiente turno**: prioriza turnos prioritarios antes que normales, por orden de llegada dentro de cada grupo (RN3).
- **Atención completa**: iniciar, pausar, reanudar y finalizar atención con observaciones y resultado obligatorio (RN5).
- **Reglas de negocio bajo concurrencia**: un turno no atendido por dos asesores al mismo tiempo (RN1) y un asesor con un solo turno activo (RN4), mediante bloqueo pesimista (`PESSIMISTIC_WRITE`) y validación de servicio.
- **Turno no atendido**: si el usuario no responde al llamado, el turno pasa a estado `NO_ATENDIDO` (RN2), mediante endpoint explícito y/o tarea programada.
- **Cálculo de tiempos**: tiempo de espera (creación → llamado) y tiempo de atención (inicio → fin, descontando pausas) persistidos y expuestos (RN6).
- **Tipos de trámite**: CRUD completo con código único y marca de prioritario por defecto.
- **Reportes**: tiempo promedio de espera, turnos atendidos/no atendidos y trámites frecuentes, con filtros por rango de fechas, asesor y tipo de trámite.
- **Asesores y ventanillas**: listado de asesores con disponibilidad y cambio de disponibilidad.
- **Seguridad**: autenticación JWT con Spring Security, autorización por rol en cada endpoint, contraseñas con BCrypt, CORS explícito, auditoría de acciones sobre turnos.

### 🔧 Mejoras

- Separación del repositorio en `/backend`, `/frontend` y `/docs`, cada parte ejecutable por separado.
- Documentación completa: `project-brief.md`, `architecture.md`, `design.md`, `security.md`, `decisions.md`, `api.md`, `setup.md`, `testing.md`, `frontend.md`.
- Pruebas unitarias JUnit 5 + Mockito: 46 tests, 100% verde, con cobertura por cada regla RN1-RN6 y criterio CA1-CA5.
- Frontend en React + TypeScript + Vite con vistas por rol (recepcionista, asesor, coordinador, administrador, ciudadano, reportes), responsive y accesible.

### 🐛 Correcciones

- Corrección de longitud del campo `documento` en el registro de usuarios (respeta restricción de BD de 10 caracteres).
- Manejo global de `DataIntegrityViolationException` (HTTP 409) en lugar de error 500.
- Rutas de seguridad compatibles con `PathPatternMatcher` de Spring Security 7 (variables de ruta al final del patrón).

---

## [0.0.1] — Línea de base inicial (commits previos en `main`)

- Entidades `Usuario`, `Asesor`, `Ciudadano`, `Ventanilla` con herencia `JOINED`.
- CRUD de ciudadanos, asesores y ventanillas.
- Manejador de excepciones global (`GlobalExceptionHandler`) con `ErrorResponse`.
- Validación Bean Validation en DTOs de entrada.
- 7 pruebas unitarias iniciales (AsesorService, CiudadanoServiceImp).
- Configuración de Pitest, JaCoCo y SonarQube en `pom.xml`.
- Documentación OpenAPI (springdoc) y consola H2 habilitadas.
