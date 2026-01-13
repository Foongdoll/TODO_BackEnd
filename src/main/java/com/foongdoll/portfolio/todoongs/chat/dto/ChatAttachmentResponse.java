package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.ChatAttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachmentResponse {
    private String attachmentId;
    private ChatAttachmentType type;
    private String name;
    private String url;
    private String mime;
    private long size;
}
