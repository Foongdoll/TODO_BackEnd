package com.foongdoll.portfolio.todoongs.security.jwt;


import com.foongdoll.portfolio.todoongs.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String jwt = (String) token.getCredentials();

        if (!jwtProvider.validate(jwt)) {
            throw new BadCredentialsException("Invalid JWT");
        }

        String email = jwtProvider.getSubject(jwt);
        UserDetails user = userDetailsService.loadUserByUsername(email);

        return new JwtAuthenticationToken(
                user,
                jwt,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
