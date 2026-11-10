# Architecture — Agendaty

## Estilo
Backend y frontend separados. Backend Spring Boot (API REST), frontend React + Vite. No es monolítico.

## Capas del backend
```
controller  →  service  →  repository  →  entity
```

## Paquetes
`com.proyecto.agendaty.controller`, `.service`, `.repository`, `.entity`, `.dto`, `.exceptions`

## Flujo de una petición
1. Controller recibe HTTP, valida con `@Valid`, llama al service.
2. Service (transaccional) aplica reglas RN1-RN6, accede al repository.
3. Repository (JPA) lee/escribe en BD (H2 en dev).
4. Controller devuelve DTO con código HTTP.

## Manejo de errores
`@RestControllerAdvice` con excepciones propias (`ResourceNotFoundException`, `BadRequestException`) → 404 / 400.

## CORS
`@CrossOrigin(origins = "http://localhost:5173")` en cada controller.

## Concurrencia (RN1/RN4)
Validación en service dentro de `@Transactional`. Si es necesario, `@Version` en `Turno`.
