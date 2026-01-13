package com.foongdoll.portfolio.todoongs.api.service;

import com.foongdoll.portfolio.todoongs.api.dto.AuthResponse;
import com.foongdoll.portfolio.todoongs.api.dto.LoginRequest;
import com.foongdoll.portfolio.todoongs.api.dto.SignUpRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse signUp(SignUpRequest request);
}
