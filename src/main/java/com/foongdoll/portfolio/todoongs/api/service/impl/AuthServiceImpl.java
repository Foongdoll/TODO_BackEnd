package com.foongdoll.portfolio.todoongs.api.service.impl;

import com.foongdoll.portfolio.todoongs.api.dto.AuthResponse;
import com.foongdoll.portfolio.todoongs.api.dto.LoginRequest;
import com.foongdoll.portfolio.todoongs.api.dto.SignUpRequest;
import com.foongdoll.portfolio.todoongs.api.entity.RefreshTokens;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.api.service.AuthService;
import com.foongdoll.portfolio.todoongs.api.service.RefreshTokenService;
import com.foongdoll.portfolio.todoongs.security.AuthProvider;
import com.foongdoll.portfolio.todoongs.security.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Override
    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        String email = normalizeEmail(request.getEmail());

        usersRepository.findByEmail(email).ifPresent((u) -> {
            throw new IllegalArgumentException("Email already registered");
        });

        Users user = Users.builder()
                .email(email)
                .pw(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider(AuthProvider.LOCAL)
                .providerId(email)
                .build();

        Users saved = usersRepository.save(user);
        return buildResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getPw() == null || !passwordEncoder.matches(request.getPassword(), user.getPw())) {
            throw new BadCredentialsException("Invalid credentials");
        }



        return buildResponse(user);
    }

    private AuthResponse buildResponse(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getPk());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("provider", user.getProvider().name());

        String token = jwtProvider.createToken(user.getEmail(), claims, false);
        String refreshToken = jwtProvider.createToken(user.getEmail(), claims, true);
        refreshTokenService.issue(user.getPk(), refreshToken);

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider().name())
                .userId(user.getPk())
                .notificationsEnabled(user.isNotificationsEnabled())
                .build();
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Refresh token is required");
        }

        String email;
        try {
            email = jwtProvider.getSubject(refreshToken);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid refresh token", ex);
        }

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        RefreshTokens stored = refreshTokenService.findValidToken(user.getPk(), refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid or expired"));

        refreshTokenService.revoke(stored);
        return buildResponse(user);
    }
}
