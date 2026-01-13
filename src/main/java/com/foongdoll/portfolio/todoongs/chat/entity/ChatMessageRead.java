package com.foongdoll.portfolio.todoongs.chat.entity;

import com.foongdoll.portfolio.todoongs.api.entity.BaseEntity;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(name = "uk_message_reader", columnNames = {"message_id", "reader_id"})
)
public class ChatMessageRead extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private Users reader;

    @Column(nullable = false)
    private LocalDateTime readAt;
}
