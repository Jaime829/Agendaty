# Decisiones técnicas (ADR) — Agendaty

Registro de decisiones, supuestos y ambigüedades resueltas durante el desarrollo.

## ADR-001: Modelo de estados del turno incluye NO_ATENDIDO y EN_PAUSA
**Estado:** Aceptado  
**Contexto:** La especificación original listaba estados `EN_ESPERA, LLAMADO, EN_ATENCION, FINALIZADO, CANCELADO` pero RN2 introduce `NO_ATENDIDO` y el módulo Atención menciona "pausa".  
**Decisión:** Se adopta la máquina de estados completa `EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO` y se registra en `docs/project-brief.md` (sección 6) y `docs/design.md` (sección 2).  
**Consecuencia:** los endpoints de atención y turnos deben manejar estos 7 estados y sus transiciones permitidas.

## ADR-002: Numeración de turnos
**Estado:** Aceptado  
**Contexto:** CA2 exige un número de turno único.  
**Decisión:** Formato `{CODIGO_TRAMITE}{P}-{NNNNN}` (tipo de trámite + prioridad + consecutivo diario de 5 dígitos), con restricción `unique` en BD. Justificación en `docs/design.md` (sección 3).

## ADR-003: Seguridad con JWT + Spring Security
**Estado:** Aceptado  
**Contexto:** El proyecto no tenía seguridad (Fase 1). Se requiere autenticación y autorización por rol (security.md).  
**Decisión:** Implementar JWT stateless con Spring Security, contraseñas BCrypt, CORS explícito, rate limiting en endpoints públicos.  
**Dependencia nueva:** `spring-boot-starter-security` (justificada en decisions.md, no en código hasta Fase 3).

## ADR-004: Bloqueo para RN1 y RN4
**Estado:** Aceptado  
**Contexto:** RN1 (un turno no atendido por dos asesores) y RN4 (un asesor, un turno activo) requieren exclusividad bajo concurrencia.  
**Decisión:** Bloqueo pesimista (`PESSIMISTIC_WRITE`) sobre turno y asesor al iniciar atención, dentro de transacción `@Transactional`, más restricción única en BD y validación de negocio previa. Detalle en `docs/architecture.md` (sección 8).

## ADR-005: RN2 — turno no atendido
**Estado:** Aceptado  
**Contexto:** Si el usuario no responde al llamado, el turno pasa a `NO_ATENDIDO`.  
**Decisión:** Mecanismo mixto: (a) endpoint explícito `/turnos/{id}/no-atendido` para recepcionista/sistema, y (b) tarea programada `@Scheduled` con tiempo configurable (`turno.no-respondido.minutos`) que marca automáticamente turnos `LLAMADO` expirados.

## ADR-006: Estructura del repositorio (Fase 5)
**Estado:** Aceptado  
**Contexto:** El backend está en la raíz del repositorio. Se requiere separación backend/frontend/docs.  
**Decisión:** Mover backend a `/backend` con `git mv`, crear `/frontend` (React + TS + Vite) y mantener `/docs`. Cada parte con su propio build y variables de entorno. `docker-compose.yml` para levantar todo junto.

## ADR-007: Base de datos para desarrollo y tests
**Estado:** Aceptado  
**Contexto:** Línea de base usa H2 en memoria.  
**Decisión:** Mantener H2 para dev y tests unitarios; perfiles `dev` y `prod` para PostgreSQL/MySQL. No se añade Testcontainers sin justificación expresa (evita dependencia de Docker en tests unitarios).

## ADR-008: Mapeo objeto-objeto
**Estado:** Aceptado  
**Contexto:** Línea de base usa ModelMapper pero los mappers existentes están hechos a mano (`@Component`).  
**Decisión:** Mantener mappers hechos a mano al estilo del proyecto (consistencia con código existente). No se añade MapStruct para no introducir arquitectura nueva sin justificación.

## Decisiones de implementación (Fase 3)

### ADR-009: Autenticación sin AuthenticationManager bean
**Estado:** Aceptado  
**Contexto:** Spring Security 7 no expone automáticamente un bean `AuthenticationManager` cuando se define un `SecurityFilterChain` personalizado; inyectarlo fallaba al iniciar el contexto.  
**Decisión:** `AuthServiceImp.login` valida la contraseña directamente con `PasswordEncoder.matches()` contra el hash almacenado, y genera el JWT con `JwtService`. Se mantiene el flujo stateless JWT.  
**Consecuencia:** no se usa `AuthenticationManager.authenticate()`; la validación es equivalente (username + password contra BCrypt).

### ADR-010: Bean java.time.Clock
**Estado:** Aceptado  
**Contexto:** Los servicios de turnos y atención necesitan un reloj inyectable para calcular tiempos (RN6) sin depender de `LocalDateTime.now()` real en tests.  
**Decisión:** Se expone un bean `@Bean Clock clock()` en `SecurityConfig` y se inyecta en `TurnoServiceImp` y `AtencionServiceImp`. En tests se inyecta un `Clock` fijo (Mockito).

### ADR-011: CORS con `setAllowedOriginPatterns`
**Estado:** Aceptado  
**Contexto:** `setAllowedOrigins` no acepta patrones con wildcard; se usa `setAllowedOriginPatterns` con el valor de `CORS_ALLOWED_ORIGINS` (por defecto `http://localhost:5173`).  
**Consecuencia:** CORS explícito y seguro, nunca `*` en producción.

### ADR-012: Endpoints de turno con `{id}` al final del patrón
**Estado:** Aceptado  
**Contexto:** `PathPatternMatcher` de Spring Security 7 no permite `**` ni variables en medio del patrón (ej. `/api/v1/turnos/**/llamar` falla).  
**Decisión:** Se usaron rutas con `{id}` al final (`/api/v1/turnos/{id}/cancelar`) y `/api/v1/turnos/llamar-siguiente` para evitar patrones intermedios incompatibles.

### ADR-013: Numeración de turnos en memoria (sin entidad ContadorTurno)
**Estado:** Aceptado  
**Contexto:** Se propuso una entidad `ContadorTurno` para el consecutivo diario.  
**Decisión:** Se calcula el consecutivo consultando turnos del día en BD y tomando el máximo sufijo numérico + 1, dentro de transacción. Suficiente para el alcance actual; se puede refinar con una tabla de contadores si hay alta concurrencia.

## Supuestos abiertos (pendiente de confirmar del usuario)
- ¿Se requiere notificación en tiempo real (WebSocket/SSE) para el monitor del coordinador, o basta con polling (actualmente polling cada 5s)?
- ¿Se usa PostgreSQL o MySQL en producción?
- ¿Existe un proveedor de identidad externo (LDAP, Keycloak) o los usuarios se gestionan en la propia BD?
