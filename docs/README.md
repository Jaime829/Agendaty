# Documentación — Agendaty

Índice de toda la documentación del proyecto.

---

## Documentos de especificidad y diseño (Fase 2 — docs-first)

| Documento | Contenido |
|-----------|-----------|
| `project-brief.md` | Especificación funcional: problema, objetivo, usuarios, módulos, reglas RN1-RN6, criterios CA1-CA5, requisitos RA1-RA7, matriz de trazabilidad |
| `architecture.md` | Estilo arquitectónico (backend + frontend separados), diagramas Mermaid, estructura de paquetes, flujo de petición, manejo de errores, perfiles, concurrencia RN1/RN4 |
| `design.md` | Modelo de dominio, máquina de estados del turno, numeración única de turnos, cálculo de tiempos, contratos de API (todos los endpoints), diseño de reportes, UI/UX por rol |
| `security.md` | Autenticación JWT-less + Spring Security simple, matriz de permisos por rol (RA7), contraseñas BCrypt (RA5), CORS/CSRF, validación, checklist OWASP Top 10 |
| `decisions.md` | Decisiones técnicas (ADR-001 a ADR-010) y supuestos abiertos |

## Documentos de implementación y operación (Fase 7)

| Documento | Contenido |
|-----------|-----------|
| `api.md` | Referencia completa de endpoints: método, ruta, roles, request, response, códigos, errores |
| `setup.md` | Requisitos, variables de entorno, credenciales del admin de desarrollo, ejecución local y Docker |
| `testing.md` | Estrategia de pruebas, cómo ejecutarlas, mapeo RN/CA/RA → tests |
| `frontend.md` | Estructura del frontend, sistema de diseño, componentes, landing y pantallas por rol |

## Documentos del repositorio

| Documento | Ubicación | Contenido |
|-----------|-----------|-----------|
| `README.md` | raíz | Descripción, estructura del repo, inicio rápido, enlaces a `/docs` |
| `CHANGELOG.md` | raíz | Registro de cambios (versión 1.0.0) |

## Enlaces rápidos

- [Inicio rápido del backend](./setup.md)
- [Inicio rápido del frontend](./frontend.md)
- [Referencia de API](./api.md)
- [Estrategia de pruebas](./testing.md)
- [Seguridad](./security.md)
- [Arquitectura](./architecture.md)
- [Diseño](./design.md)
- [Decisiones técnicas](./decisions.md)
