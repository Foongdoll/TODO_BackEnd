package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.UserNotificationRequest;
import com.foongdoll.portfolio.todoongs.chat.service.CurrentUserService;
import com.foongdoll.portfolio.todoongs.chat.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/settings")
@RequiredArgsConstructor
public class ChatSettingsController {

    private final CurrentUserService currentUserService;
    private final UserSettingsService userSettingsService;

    @PostMapping("/notifications")
    public ResponseEntity<Void> updateNotifications(@RequestBody UserNotificationRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        userSettingsService.updateNotificationSetting(currentUser, request.isEnabled());
        return ResponseEntity.noContent().build();
    }
}
