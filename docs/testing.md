# Estrategia de pruebas — Agendaty

## Backend

### Framework y librerías
- **JUnit 5** (Jupiter) + **Mockito** (vía `spring-boot-starter-test`).
- No se agregó JaCoCo como dependencia nueva (ya estaba en `pom.xml`).

### Convenciones de prueba
- Patrón **Arrange-Act-Assert**.
- Nombres descriptivos en español: `deberiaLanzarExcepcion_cuando...`.
- Un propósito por test, sin dependencias entre tests.
- `Mockito` solo para dependencias externas a la unidad probada.
- Reloj inyectable (`java.time.Clock`) para cálculos de tiempo (RN6).

### Ejecución

```powershell
cd backend
./mvnw test
```

### Resultado (línea actual)

| Clase de prueba | Tests |
|-----------------|-------|
| AgendatyApplicationTests | 1 |
| AtencionControllerTest | 4 |
| TurnoControllerTest | 5 |
| AtencionServiceImpTest | 8 |
| CiudadanoServiceImpTest | 3 |
| TipoTramiteServiceImpTest | 5 |
| TurnoServiceImpTest | 11 |
| AuthServiceTest | 7 |
| **Total** | **44** |

Todos: **44 tests, 0 fallos, 100% verde**.

### Mapeo RN/CA/RA → pruebas

| Requisito | Prueba que lo valida |
|-----------|----------------------|
| CA1-CA4 | `TurnoServiceImpTest.generarTurno_*` |
| CA5 | `TurnoServiceImpTest.llamarSiguiente_*` |
| RN1 | `AtencionServiceImpTest.iniciarAtencion_cuandoTurnoYaAtendidoPorOtroAsesor_*` |
| RN2 | `TurnoServiceImpTest.marcarNoAtendido_cuandoTurnoEstaLlamado_*` |
| RN3 | `TurnoServiceImpTest.llamarSiguiente_deberiaPriorizarTurnosPrioritarios_*` |
| RN4 | `TurnoServiceImpTest.llamarSiguiente_cuandoAsesorTieneTurnoActivo_*` + `AtencionServiceImpTest.iniciarAtencion_cuandoAsesorYaTieneTurnoActivo_*` |
| RN5 | `AtencionServiceImpTest.finalizarAtencion_sinResultado_*` |
| RN6 | `AtencionServiceImpTest.finalizarAtencion_deberiaCalcularTiempos_*` |
| RA1 | `AuthServiceTest.registrar_rolCIUDADANOForzado_aunqueSeEnvieOtroRol` |
| RA2 | `AuthServiceTest.login_credencialesCorrectas` / `login_credencialesIncorrectas` / `login_usuarioNoExiste` |
| RA4 | `AuthServiceTest.crearUsuarioInterno_adminRegistraAsesor` |
| RA5 | `AuthServiceTest.registrar_contraseñaSeGuardaConBCrypt` |
| RA6 | `AuthServiceTest.crearUsuarioInterno_usuarioNoAdmin_lanzaExcepcion` |

## Frontend

- **Vitest** + **Testing Library** para componentes críticos.
- Ejecutar: `cd frontend && npm run test`.
