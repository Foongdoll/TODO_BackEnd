package com.foongdoll.portfolio.todoongs.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private String email;
    private String name;
    private String provider;
    private String userId;
    private boolean notificationsEnabled;
}
