package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotificationEvent {
    private String roomId;
    private String messageId;
    private String senderName;
    private String preview;
    private ChatMessageType type;
    private LocalDateTime createdAt;
}
