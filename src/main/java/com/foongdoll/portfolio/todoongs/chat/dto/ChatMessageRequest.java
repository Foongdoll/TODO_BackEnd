package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.ChatMessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {
    private String roomId;
    private ChatMessageType type = ChatMessageType.TEXT;
    private String content;
    private String payload;
    private List<String> attachmentIds;
}
