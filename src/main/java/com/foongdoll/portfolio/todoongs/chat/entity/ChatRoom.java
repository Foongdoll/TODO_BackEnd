package com.foongdoll.portfolio.todoongs.chat.entity;

import com.foongdoll.portfolio.todoongs.api.entity.BaseEntity;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.model.ChatRoomType;
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
public class ChatRoom extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    @Column(nullable = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;
}
