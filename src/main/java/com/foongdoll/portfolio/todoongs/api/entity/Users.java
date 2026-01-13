package com.foongdoll.portfolio.todoongs.api.entity;

import com.foongdoll.portfolio.todoongs.security.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String pw;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    @Column
    private String pictureUrl;

    @Column(nullable = false)
    private boolean notificationsEnabled = true;
}
