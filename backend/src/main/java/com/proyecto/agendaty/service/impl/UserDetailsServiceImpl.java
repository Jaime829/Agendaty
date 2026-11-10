package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.entity.Usuario;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CiudadanoRepository ciudadanoRepository;
    private final AsesorRepository asesorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = ciudadanoRepository.findByUsername(username)
                .map(u -> (Usuario) u)
                .orElseGet(() -> asesorRepository.findByUsername(username)
                        .map(u -> (Usuario) u)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username)));

        return new User(
                usuario.getUsername(),
                usuario.getPassword() != null ? usuario.getPassword() : "",
                List.of(new SimpleGrantedAuthority(usuario.getRol().name()))
        );
    }
}
