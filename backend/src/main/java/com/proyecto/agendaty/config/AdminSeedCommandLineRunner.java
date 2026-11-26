package com.proyecto.agendaty.config;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.entity.TipoTramite;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.TipoTramiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * RA3: Admin inicial (seed). Al arrancar el backend, si no existe un administrador,
 * se crea uno automáticamente con credenciales de variables de entorno o valores por defecto
 * SOLO para desarrollo.
 */
@Component
public class AdminSeedCommandLineRunner implements CommandLineRunner {

    private final CiudadanoRepository ciudadanoRepository;
    private final AsesorRepository asesorRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeedCommandLineRunner(CiudadanoRepository ciudadanoRepository,
                                      AsesorRepository asesorRepository,
                                      TipoTramiteRepository tipoTramiteRepository,
                                      PasswordEncoder passwordEncoder) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.asesorRepository = asesorRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = System.getenv("ADMIN_EMAIL");
        if (adminEmail == null || adminEmail.isBlank()) {
            adminEmail = "admin@turnos.local"; // valor por defecto SOLO desarrollo
        }

        String adminPassword = System.getenv("ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            adminPassword = "Admin123*"; // valor por defecto SOLO desarrollo
        }

        // Si ya existe un usuario con ese correo, no hacer nada
        Optional<Ciudadano> existente = ciudadanoRepository.findByCorreo(adminEmail);
        if (existente.isPresent()) {
            return;
        }

        // También verificar en asesores por si acaso
        Optional<Asesor> existenteAsesor = asesorRepository.findByCorreo(adminEmail);
        if (existenteAsesor.isPresent()) {
            return;
        }

        // Crear el administrador semilla (usamos Ciudadano como entidad base con rol ADMINISTRADOR)
        com.proyecto.agendaty.entity.Ciudadano admin = new com.proyecto.agendaty.entity.Ciudadano();
        admin.setNombre("Administrador Sistema");
        admin.setCorreo(adminEmail);
        admin.setUsername(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol(RolUsuario.ROLE_ADMINISTRADOR);
        admin.setDocumento("ADMIN001");
        admin.setTelefono("0000000000");

        ciudadanoRepository.save(admin);

        System.out.println("=== SEED ADMIN CREADO ===");
        System.out.println("Correo: " + adminEmail);
        System.out.println("Contraseña: " + adminPassword);
        System.out.println("⚠️  Cambie estas credenciales en producción");
        System.out.println("========================");

        // Crear tipos de trámite por defecto si no existen
        if (tipoTramiteRepository.count() == 0) {
            TipoTramite cedula = new TipoTramite();
            cedula.setNombre("Cédula de Identidad");
            cedula.setCodigo("CED");
            cedula.setEsPrioritarioPorDefecto(false);
            cedula.setActivo(true);
            tipoTramiteRepository.save(cedula);

            TipoTramite pasaporte = new TipoTramite();
            pasaporte.setNombre("Pasaporte");
            pasaporte.setCodigo("PAS");
            pasaporte.setEsPrioritarioPorDefecto(false);
            pasaporte.setActivo(true);
            tipoTramiteRepository.save(pasaporte);

            TipoTramite licencia = new TipoTramite();
            licencia.setNombre("Licencia de Conducir");
            licencia.setCodigo("LIC");
            licencia.setEsPrioritarioPorDefecto(false);
            licencia.setActivo(true);
            tipoTramiteRepository.save(licencia);

            System.out.println("=== SEED TIPOS DE TRÁMITE CREADOS ===");
            System.out.println("Cédula de Identidad (CED)");
            System.out.println("Pasaporte (PAS)");
            System.out.println("Licencia de Conducir (LIC)");
            System.out.println("=====================================");
        }
    }
}
