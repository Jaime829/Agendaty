package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
}
