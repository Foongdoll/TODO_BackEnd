package com.foongdoll.portfolio.todoongs.chat.entity;

import com.foongdoll.portfolio.todoongs.api.entity.BaseEntity;
import com.foongdoll.portfolio.todoongs.chat.model.ChatAttachmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatAttachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatAttachmentType type = ChatAttachmentType.FILE;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = true)
    private String mime;

    @Column(nullable = false)
    private long size;
}
