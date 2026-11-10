package com.proyecto.agendaty.config;

import com.proyecto.agendaty.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Clock;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${cors.allowed.origins:http://localhost:5173}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tipos-tramite").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                // Turnos
                .requestMatchers(HttpMethod.GET, "/api/v1/turnos").hasAnyRole("RECEPCIONISTA", "ASESOR", "COORDINADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/turnos").hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/turnos/llamar-siguiente").hasRole("ASESOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/turnos/{id}/cancelar").hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/turnos/{id}/no-atendido").hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/turnos/{id}").authenticated()
                // Atención
                .requestMatchers(HttpMethod.PUT, "/api/v1/atenciones/iniciar").hasRole("ASESOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/atenciones/{turnoId}/pausar").hasRole("ASESOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/atenciones/{turnoId}/reanudar").hasRole("ASESOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/atenciones/{turnoId}/finalizar").hasRole("ASESOR")
                // Asesores
                .requestMatchers(HttpMethod.GET, "/api/v1/asesores").hasAnyRole("ASESOR", "RECEPCIONISTA", "COORDINADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/asesores/{id}/disponible").hasRole("ADMINISTRADOR")
                // Ventanillas
                .requestMatchers(HttpMethod.GET, "/api/v1/ventanillas").hasAnyRole("ASESOR", "RECEPCIONISTA", "COORDINADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/ventanillas").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/ventanillas/{id}").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ventanillas/{id}").hasRole("ADMINISTRADOR")
                // Reportes
                .requestMatchers(HttpMethod.GET, "/api/v1/reportes/**").hasAnyRole("COORDINADOR", "ADMINISTRADOR")
                // Usuarios
                .requestMatchers(HttpMethod.GET, "/api/v1/usuarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/{id}/rol").hasRole("ADMINISTRADOR")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // En desarrollo se permite el origen configurado; en producción ajustar CORS_ALLOWED_ORIGINS
        configuration.setAllowedOriginPatterns(List.of(corsAllowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Origin", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
