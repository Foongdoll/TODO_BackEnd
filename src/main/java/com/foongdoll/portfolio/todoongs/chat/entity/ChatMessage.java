package com.foongdoll.portfolio.todoongs.chat.entity;

import com.foongdoll.portfolio.todoongs.api.entity.BaseEntity;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.model.ChatMessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageType type = ChatMessageType.TEXT;

    @Lob
    @Column(nullable = true)
    private String content;

    @Lob
    @Column(nullable = true)
    private String payload;

    @Column(nullable = false)
    private boolean deleted = false;
}
