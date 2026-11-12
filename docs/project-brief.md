# Project Brief — Agendaty

Plataforma de gestión de turnos y atención al ciudadano.

## Problema
Una entidad pública, institución educativa o centro de servicios atiende usuarios por orden de llegada o mediante listas manuales. Esto genera congestión, pérdida de turnos, tiempos de espera altos, poca trazabilidad de la atención y dificultad para medir la calidad del servicio.

## Objetivo
Construir una plataforma que permita registrar solicitudes de atención, asignar turnos, gestionar ventanillas o asesores, controlar estados de atención y generar reportes de tiempos de espera.

## Usuarios
- Ciudadano o usuario solicitante
- Asesor
- Administrador
- Coordinador de atención
- Recepcionista

## Módulos
| Módulo | Funcionalidades |
|--------|-----------------|
| Solicitudes | Registro del trámite o motivo de atención |
| Turnos | Generación, llamado y cancelación de turnos |
| Atención | Inicio, pausa, finalización y observaciones |
| Asesores | Disponibilidad, ventanillas y carga de atención |
| Estados | En espera, llamado, en atención, finalizado, cancelado, no atendido, en pausa |
| Reportes | Tiempo promedio de espera, turnos atendidos, trámites frecuentes |

## Reglas de negocio (RN)
- RN1: Un turno no puede ser atendido por dos asesores al mismo tiempo.
- RN2: Si el usuario no responde al llamado, el turno pasa a estado "No atendido".
- RN3: Los turnos prioritarios deben pasar antes que los turnos normales.
- RN4: Un asesor solo puede tener un turno activo.
- RN5: Toda atención finalizada debe tener resultado registrado.
- RN6: El sistema debe calcular el tiempo de espera y el tiempo de atención.

## Requisitos adicionales (RA)
- RA1: Registro público. Cualquier persona puede registrarse (nombre, documento, correo, contraseña). Rol por defecto SIEMPRE CIUDADANO. El backend ignora cualquier rol enviado en el registro público.
- RA2: Login simple. Correo + contraseña. Si es correcto, devuelve datos del usuario (id, nombre, correo, rol). El frontend guarda la sesión (Context + localStorage) y redirige según el rol.
- RA3: Admin inicial (seed). Al arrancar el backend, si no existe un administrador, se crea uno automáticamente con credenciales de `application.properties` / variables de entorno (`ADMIN_EMAIL`, `ADMIN_PASSWORD`). Usa `CommandLineRunner` simple. Credenciales de desarrollo documentadas en `docs/setup.md` con advertencia de cambiarlas en producción.
- RA4: Gestión por el administrador. Solo ADMINISTRADOR puede registrar asesores y demás usuarios internos (RECEPCIONISTA, COORDINADOR, ADMINISTRADOR) mediante `POST /admin/usuarios`, además de gestionar ventanillas, tipos de trámite y disponibilidad/asignación de asesores. Estos usuarios internos no se crean por el registro público.
- RA5: Contraseñas. Se guardan con BCrypt (usa solo `spring-security-crypto`, sin activar Spring Security completo). Nunca se devuelven en las respuestas.
- RA6: Autorización simple. Cada petición protegida envía el header `X-Usuario-Id`; el service busca el usuario y valida que su rol tenga permiso (403 si no). Esquema didáctico; en producción se usaría JWT/Spring Security.
- RA7: Roles y permisos (matriz en `docs/security.md`).

## Historia de usuario (recepcionista)
Como recepcionista, quiero generar un turno según el tipo de trámite para organizar la atención de los usuarios.

| CA | Criterio |
|----|----------|
| CA1 | Seleccionar tipo de trámite |
| CA2 | Número de turno único |
| CA3 | Estado "En espera" |
| CA4 | Diferenciar normal/prioritario |
| CA5 | Asesor llama siguiente disponible |

## Modelo de estados del turno
EN_ESPERA, LLAMADO, EN_ATENCION, EN_PAUSA, FINALIZADO, CANCELADO, NO_ATENDIDO.

## Matriz de trazabilidad (se completa en Fase 5)

| Req. | Clase/Endpoint | Pantalla | Test |
|------|---------------|----------|------|
| CA1 | TurnoController.generarTurno | Recepcionista | — |
| CA2 | TurnoController.generarTurno | Recepcionista | — |
| CA3 | TurnoController.generarTurno | Recepcionista | — |
| CA4 | TurnoController.generarTurno | Recepcionista | — |
| CA5 | TurnoController.llamarSiguiente | Asesor | — |
| RN1 | AtencionService.iniciarAtencion | Asesor | — |
| RN2 | TurnoController.marcarNoAtendido | Recepcionista | — |
| RN3 | TurnoService.llamarSiguiente | Asesor | — |
| RN4 | AtencionService.iniciarAtencion | Asesor | — |
| RN5 | AtencionService.finalizarAtencion | Asesor | — |
| RN6 | AtencionService.finalizarAtencion | Asesor/Coordinador | — |
| RA1 | AuthController.registro | Registro | — |
| RA2 | AuthController.login | Login | — |
| RA3 | AdminSeedCommandLineRunner | (seed automático) | — |
| RA4 | AdminController.registrarUsuario | Administrador | — |
| RA5 | Usuario (password BCrypt) | (BD) | — |
| RA6 | Service.validarRol (X-Usuario-Id) | (todas protegidas) | — |
| RA7 | Matriz de permisos | (todas) | — |

## Convenciones del proyecto
- Java 25, Spring Boot 4.1.1, Maven.
- Paquete base: `com.proyecto.agendaty`.
- Capas: controller → service → repository → entity → dto → exceptions.
- Inyección por constructor (sin @Autowired en campos).
- `@CrossOrigin(origins = "http://localhost:5173")` en cada controller.
- Rutas cortas (sin prefijo `/api/v1`).
- Bean Validation en DTOs de entrada.
- `@RestControllerAdvice` para manejo global de excepciones.
- Base de datos H2 en memoria para desarrollo.
