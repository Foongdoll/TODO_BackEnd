package com.foongdoll.portfolio.todoongs.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            // Provider에 위임
            JwtAuthenticationToken jwtAuthRequest = new JwtAuthenticationToken(token);
            Authentication result = authenticationManager.authenticate(jwtAuthRequest);

            if (result != null && result.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(result);
            }
        }

        filterChain.doFilter(request, response);
    }
}