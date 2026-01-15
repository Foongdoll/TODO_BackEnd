package com.foongdoll.portfolio.todoongs.api.service;

import com.foongdoll.portfolio.todoongs.api.entity.RefreshTokens;
import com.foongdoll.portfolio.todoongs.api.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-exp-ms}")
    private long refreshTokenExpMs;

    @Transactional
    public RefreshTokens issue(String userPk, String rawToken) {
        LocalDateTime now = LocalDateTime.now();
        revokeExisting(userPk, now);

        String tokenHash = sha256Hex(rawToken);

        RefreshTokens token = RefreshTokens.builder()
                .userPk(userPk)
                .refreshTokenHash(tokenHash)
                .expiresAt(now.plus(Duration.ofMillis(refreshTokenExpMs)))
                .build();

        return tokenRepository.save(token);
    }

    public Optional<RefreshTokens> findValidToken(String userPk, String rawToken) {
        LocalDateTime now = LocalDateTime.now();
        String tokenHash = sha256Hex(rawToken);

        return tokenRepository.findByUserPkOrderByCreatedDesc(userPk).stream()
                .filter(t -> t.getRevokedAt() == null)
                .filter(t -> t.getExpiresAt() == null || t.getExpiresAt().isAfter(now))
                .filter(t -> tokenHash.equals(t.getRefreshTokenHash()))
                .findFirst();
    }

    /** raw token -> SHA-256 hex(64 chars) */
    private String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // JVM에 SHA-256 없을 가능성은 사실상 없지만, 실패 시 즉시 터뜨리는 게 안전
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Transactional
    public void revoke(RefreshTokens token) {
        token.setRevokedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }

    private void revokeExisting(String userPk, LocalDateTime now) {
        List<RefreshTokens> tokens = tokenRepository.findByUserPkAndRevokedAtIsNull(userPk);
        if (tokens.isEmpty()) {
            return;
        }

        tokens.forEach(token -> token.setRevokedAt(now));
        tokenRepository.saveAll(tokens);
    }
}
