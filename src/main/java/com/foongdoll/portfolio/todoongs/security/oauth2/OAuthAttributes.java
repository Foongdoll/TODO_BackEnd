package com.foongdoll.portfolio.todoongs.security.oauth2;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.security.AuthProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final String providerId;
    private final AuthProvider provider;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.from(registrationId);
        return switch (provider) {
            case KAKAO -> ofKakao(attributes, userNameAttributeName);
            case NAVER -> ofNaver(attributes, userNameAttributeName);
            default -> ofDefault(attributes, userNameAttributeName, provider);
        };
    }

    private static OAuthAttributes ofDefault(Map<String, Object> attributes, String attributeKey, AuthProvider provider) {
        return builder()
                .attributes(new HashMap<>(attributes))
                .nameAttributeKey(attributeKey)
                .name(readString(attributes, "name", "nickname"))
                .email(sanitizeEmail(readString(attributes, "email")))
                .picture(readString(attributes, "picture", "profile_image"))
                .providerId(String.valueOf(attributes.getOrDefault("sub", attributes.getOrDefault("id", ""))))
                .provider(provider)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes, String attributeKey) {
        Map<String, Object> kakaoAccount = cast(attributes.get("kakao_account"));
        Map<String, Object> profile = cast(kakaoAccount.get("profile"));

        return builder()
                .attributes(new HashMap<>(attributes))
                .nameAttributeKey(attributeKey)
                .name(readString(profile, "nickname"))
                .email(sanitizeEmail(readString(kakaoAccount, "email")))
                .picture(readString(profile, "profile_image_url"))
                .providerId(String.valueOf(attributes.getOrDefault("id", "")))
                .provider(AuthProvider.KAKAO)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes, String attributeKey) {
        Map<String, Object> response = cast(attributes.get("response"));

        return builder()
                .attributes(new HashMap<>(response))
                .nameAttributeKey(attributeKey)
                .name(readString(response, "name"))
                .email(sanitizeEmail(readString(response, "email")))
                .picture(readString(response, "profile_image"))
                .providerId(readString(response, "id"))
                .provider(AuthProvider.NAVER)
                .build();
    }

    public Map<String, Object> toAttributeMapWithUser(Users user) {
        Map<String, Object> enriched = new LinkedHashMap<>(attributes);
        enriched.put("userId", user.getPk());
        enriched.put("email", user.getEmail());
        enriched.put("name", user.getName());
        enriched.put("provider", user.getProvider().name());
        enriched.put("providerId", user.getProviderId());
        return enriched;
    }

    public Users toEntity() {
        return Users.builder()
                .email(email)
                .name(defaultName(name, email))
                .provider(provider)
                .providerId(providerId)
                .pictureUrl(picture)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private static String readString(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value instanceof String str && !str.isBlank()) {
                return str;
            }
        }
        return "";
    }

    private static String sanitizeEmail(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim().toLowerCase(Locale.ROOT);
        return trimmed.isBlank() ? "" : trimmed;
    }

    private static String defaultName(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
