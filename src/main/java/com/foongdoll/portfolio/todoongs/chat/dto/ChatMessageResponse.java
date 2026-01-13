package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String messageId;
    private String roomId;
    private String senderId;
    private String senderName;
    private ChatMessageType type;
    private String content;
    private String payload;
    private List<ChatAttachmentResponse> attachments;
    private Set<String> readUserIds;
    private LocalDateTime createdAt;
}
