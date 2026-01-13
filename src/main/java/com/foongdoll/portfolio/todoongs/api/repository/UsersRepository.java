package com.foongdoll.portfolio.todoongs.api.repository;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.security.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByProviderAndProviderId(AuthProvider provider, String providerId);

    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nameKeyword, String emailKeyword);
}
