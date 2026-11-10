# Estrategia de pruebas — Agendaty

---

## Backend

### Framework y librerías

- **JUnit 5** (Jupiter) + **Mockito** (vía `spring-boot-starter-test`).
- No se agregó JaCoCo como dependencia nueva: ya está configurada en `pom.xml` (reporte en `target/site/jacoco/`).
- El proyecto ya tenía configurado Pitest y SonarQube en `pom.xml`.

### Convenciones de prueba

- Patrón **Arrange-Act-Assert**.
- Nombres descriptivos en español: `deberiaLanzarExcepcion_cuando...`, `deberiaRetornar..._cuando...`.
- Un propósito por test, sin dependencias entre tests.
- `Mockito` solo para dependencias externas a la unidad probada.
- Reloj inyectable (`java.time.Clock`) para no depender de la hora real en cálculos de tiempo (RN6).

### Ejecución

```powershell
cd backend
./mvnw test
```

### Resultado de línea de base

| Clase de prueba | Tests |
|-----------------|-------|
| `AgendatyApplicationTests` | 1 |
| `AsesorServiceTest` | 3 |
| `CiudadanoServiceImpTest` | 3 |
| **Subtotal línea de base** | **7** |

### Resultado tras la implementación (Fase 4)

| Clase de prueba | Tests | Qué valida |
|-----------------|-------|------------|
| `TurnoServiceImpTest` | 11 | CA1-CA4, CA5, RN2, RN3, RN4, auditoría |
| `AtencionServiceImpTest` | 8 | RN1, RN4, RN5, RN6, pausa/reanudar, auditoría |
| `TipoTramiteServiceImpTest` | 5 | CRUD tipos de trámite |
| `AuthServiceTest` | 6 | login, register, validaciones |
| `TurnoControllerTest` | 5 | endpoints de turnos (códigos HTTP) |
| `AtencionServiceImpTest` | 8 | … |
| `AtencionControllerTest` | 4 | endpoints de atención (códigos HTTP) |
| **Subtotal nuevo** | **39** | |
| **Total** | **46** | **0 fallos, 100% verde** |

### Cobertura JaCoCo (resumen)

Clases con cobertura alta (>70% líneas): `TurnoServiceImp`, `AtencionServiceImp`, `AuthServiceImp`, `TipoTramiteServiceImp`, `TurnoController`, `AtencionController`, `SecurityConfig`, `AsesorServiceImp`, `CiudadanoServiceImp`.

Reporte completo: `backend/target/site/jacoco/index.html`.

---

## Mapeo RN/CA → pruebas

| Requisito | Prueba que lo valida |
|-----------|----------------------|
| CA1 | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` |
| CA2 | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` (numeroTurno) |
| CA3 | `TurnoServiceImpTest.generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3` (estado EN_ESPERA) |
| CA4 | `TurnoServiceImpTest.generarTurno_deberiaDiferenciarPrioritarioYNormal_CA4` |
| CA5 | `TurnoServiceImpTest.llamarSiguiente_deberiaDevolverElSiguienteTurnoDisponible_CA5` |
| RN1 | `AtencionServiceImpTest.iniciarAtencion_cuandoTurnoYaAtendidoPorOtroAsesor_deberiaRechazar_RN1` |
| RN2 | `TurnoServiceImpTest.marcarNoAtendido_cuandoTurnoEstaLlamado_deberiaPasarANO_ATENDIDO_RN2` |
| RN3 | `TurnoServiceImpTest.llamarSiguiente_deberiaPriorizarTurnosPrioritarios_RN3` |
| RN4 | `TurnoServiceImpTest.llamarSiguiente_cuandoAsesorTieneTurnoActivo_deberiaRechazar_RN4` y `AtencionServiceImpTest.iniciarAtencion_cuandoAsesorYaTieneTurnoActivo_deberiaLanzarExcepcion_RN4` |
| RN5 | `AtencionServiceImpTest.finalizarAtencion_sinResultado_deberiaLanzarExcepcion_RN5` |
| RN6 | `AtencionServiceImpTest.finalizarAtencion_deberiaCalcularTiemposEsperaYAtencion_RN6` |

---

## Frontend

- **Vitest** + **Testing Library** para componentes y lógica crítica.
- Ejecutar: `cd frontend && npm run test`.
- Los componentes críticos (login, generación de turno, atención) deben cubrir caso feliz, error y vacío.
