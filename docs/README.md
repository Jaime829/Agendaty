# Documentación — Agendaty

Índice de toda la documentación del proyecto.

---

## Documentos de especificidad y diseño (Fase 2 — docs-first)

| Documento | Contenido |
|-----------|-----------|
| `project-brief.md` | Especificación funcional: problema, objetivo, usuarios, módulos, reglas de negocio RN1-RN6, criterios CA1-CA5, matriz de trazabilidad |
| `architecture.md` | Estilo arquitectónico (backend + frontend separados), diagramas Mermaid, estructura de paquetes, flujo de petición, manejo de errores, perfiles, concurrencia RN1/RN4, cálculo turno (RN3), tiempos (RN6) |
| `design.md` | Modelo de dominio, máquina de estados del turno, numeración única de turnos, cálculo de tiempos, contratos de API, diseño de reportes, UI/UX por rol |
| `security.md` | Autenticación JWT + Spring Security, matriz de permisos por rol, contraseñas BCrypt, CORS/CSRF, validación, rate limiting, auditoría, checklist OWASP Top 10 |
| `decisions.md` | Decisiones técnicas (ADR-001 a ADR-013) y supuestos abiertos |

---

## Documentos de implementación y operación (Fase 9)

| Documento | Contenido |
|-----------|-----------|
| `api.md` | Referencia completa de endpoints: método, ruta, roles, request, response, códigos, errores, ejemplos |
| `setup.md` | Requisitos, variables de entorno, ejecución local (backend/frontend), pruebas, build, Docker Compose |
| `testing.md` | Estrategia de pruebas (JUnit 5 + Mockito, Vitest), cómo ejecutarlas, mapeo RN/CA → pruebas, cobertura JaCoCo |
| `frontend.md` | Estructura del frontend, convenciones, componentes, vistas por rol, calidad UI/UX |

---

## Documentos del repositorio

| Documento | Ubicación | Contenido |
|-----------|-----------|-----------|
| `README.md` | raíz | Descripción, estructura del repo, inicio rápido, enlaces a `/docs` |
| `CHANGELOG.md` | raíz | Registro de cambios generado a partir de los commits (skill changelog-generator) |

---

## Estructura del repositorio

```
/backend      → Spring Boot (API REST), build propio, config, README
/frontend     → React + TypeScript + Vite, consume la API, .env.example, README
/docs         → toda la documentación
docker-compose.yml → backend + frontend + base de datos
```

## Enlaces rápidos

- [Inicio rápido del backend](./setup.md)
- [Inicio rápido del frontend](./frontend.md)
- [Referencia de API](./api.md)
- [Estrategia de pruebas](./testing.md)
- [Seguridad](./security.md)
- [Arquitectura](./architecture.md)
- [Diseño](./design.md)
- [Decisiones técnicas](./decisions.md)
