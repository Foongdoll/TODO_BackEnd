package com.foongdoll.portfolio.todoongs.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private String userId;
    private String email;
    private String name;
}
