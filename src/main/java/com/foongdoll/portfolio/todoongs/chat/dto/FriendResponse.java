package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponse {
    private String userId;
    private String email;
    private String name;
    private FriendStatus status;
    private boolean online;
}
