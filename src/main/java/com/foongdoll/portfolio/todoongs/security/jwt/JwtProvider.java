package com.foongdoll.portfolio.todoongs.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-exp-ms}")
    private long accessTokenExpMs;

    @Value("${jwt.refresh-token-exp-ms}")
    private long refreshTokenExpMs;

    @Value("${jwt.issuer}")
    private String issuer;

    private Key key;

    @PostConstruct
    void init() {
        byte[] bytes = Decoders.BASE64.decode(toBase64IfNeeded(secret));
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    // secret이 base64가 아닐 수도 있어서 최소한의 보정
    private String toBase64IfNeeded(String s) {
        // 이미 base64면 그대로 두고, 아니면 base64 인코딩해서 사용하고 싶을 수 있는데
        // 여기서는 "사용자 입력이 base64라고 가정"이 더 안전함.
        // 너가 plain text로 쓰고 있다면 secret을 base64로 바꿔서 넣는 걸 추천.
        return s;
    }

    public String createToken(String subject, Map<String, Object> claims, boolean refresh) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (refresh ? refreshTokenExpMs : accessTokenExpMs));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject) // 보통 email
                .setIssuedAt(now)
                .setExpiration(exp)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }
}