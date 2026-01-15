package com.foongdoll.portfolio.todoongs.api.repository;

import com.foongdoll.portfolio.todoongs.api.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<RefreshTokens, Long> {
    List<RefreshTokens> findByUserPkOrderByCreatedDesc(String userPk);

    List<RefreshTokens> findByUserPkAndRevokedAtIsNull(String userPk);
}
