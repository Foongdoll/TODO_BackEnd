package com.foongdoll.portfolio.todoongs.security.dto;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter@ToString
@Builder@NoArgsConstructor@AllArgsConstructor
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private String email;
    private String pw;
    private Collection<? extends GrantedAuthority> authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authority;
    }

    @Override
    public @Nullable String getPassword() {
        return pw;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
