package com.proyecto.agendaty.config;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
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
    private final PasswordEncoder passwordEncoder;

    public AdminSeedCommandLineRunner(CiudadanoRepository ciudadanoRepository,
                                      AsesorRepository asesorRepository,
                                      PasswordEncoder passwordEncoder) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.asesorRepository = asesorRepository;
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
    }
}
