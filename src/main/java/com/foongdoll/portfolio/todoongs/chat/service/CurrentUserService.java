package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.security.dto.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsersRepository usersRepository;

    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails details) {
            email = details.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User details) {
            email = details.getUsername();
        } else if (principal instanceof String value) {
            email = value;
        } else {
            throw new IllegalStateException("Unsupported principal");
        }

        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public Users getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            return getCurrentUser();
        }
        Object innerPrincipal = principal;
        String email;
        if (innerPrincipal instanceof UserDetails details) {
            email = details.getUsername();
        } else if (innerPrincipal instanceof org.springframework.security.core.userdetails.User details) {
            email = details.getUsername();
        } else {
            email = principal.getName();
        }
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
