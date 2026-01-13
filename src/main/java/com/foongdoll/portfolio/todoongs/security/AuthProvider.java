package com.foongdoll.portfolio.todoongs.security;

import java.util.Locale;

public enum AuthProvider {
    LOCAL,
    GOOGLE,
    NAVER,
    KAKAO,
    GITHUB,
    UNKNOWN;

    public static AuthProvider from(String registrationId) {
        if (registrationId == null) {
            return UNKNOWN;
        }

        return switch (registrationId.toLowerCase(Locale.ROOT)) {
            case "google" -> GOOGLE;
            case "naver" -> NAVER;
            case "kakao" -> KAKAO;
            case "github" -> GITHUB;
            case "local" -> LOCAL;
            default -> UNKNOWN;
        };
    }

    public boolean isLocal() {
        return this == LOCAL;
    }
}
