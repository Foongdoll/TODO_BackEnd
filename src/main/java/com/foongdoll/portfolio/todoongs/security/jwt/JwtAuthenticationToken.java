package com.foongdoll.portfolio.todoongs.security.jwt;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String token;

    public JwtAuthenticationToken(String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = null;
        this.token = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal,
                                  String token,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
