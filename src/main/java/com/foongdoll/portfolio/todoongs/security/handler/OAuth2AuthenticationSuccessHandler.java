package com.foongdoll.portfolio.todoongs.security.handler;

import tools.jackson.databind.ObjectMapper;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String userId = principal.getAttribute("userId");
        Users user = usersRepository.findById(userId).orElseThrow(() -> new IllegalStateException("OAuth user not found"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getPk());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("provider", user.getProvider().name());

        String token = jwtProvider.createToken(user.getEmail(), claims, false);

        Map<String, Object> payload = new HashMap<>();
        payload.put("accessToken", token);
        payload.put("provider", user.getProvider().name());
        payload.put("email", user.getEmail());
        payload.put("name", user.getName());
        payload.put("userId", user.getPk());
        payload.put("notificationsEnabled", user.isNotificationsEnabled());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), payload);
    }
}
