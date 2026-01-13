package com.foongdoll.portfolio.todoongs.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceEvent {
    private String userId;
    private boolean online;
    private LocalDateTime at;
}
