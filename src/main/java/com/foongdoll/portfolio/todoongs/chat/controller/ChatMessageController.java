package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.ReadReceiptRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.ReadReceiptEvent;
import com.foongdoll.portfolio.todoongs.chat.service.ChatMessageService;
import com.foongdoll.portfolio.todoongs.chat.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final CurrentUserService currentUserService;
    private final ChatMessageService chatMessageService;

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> listMessages(
            @PathVariable String roomId,
            @RequestParam(name = "limit", defaultValue = "50") int limit
    ) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatMessageService.listMessages(currentUser, roomId, limit));
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatMessageService.sendMessage(currentUser, request));
    }

    @PostMapping("/reads")
    public ResponseEntity<ReadReceiptEvent> markRead(@RequestBody ReadReceiptRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatMessageService.markRead(currentUser, request.getRoomId(), request.getMessageIds()));
    }
}
