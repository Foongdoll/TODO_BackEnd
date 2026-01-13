package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.ReadReceiptRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.ReadReceiptEvent;
import com.foongdoll.portfolio.todoongs.chat.service.ChatMessageService;
import com.foongdoll.portfolio.todoongs.chat.service.CurrentUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final CurrentUserService currentUserService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public ChatMessageResponse send(ChatMessageRequest request, Principal principal) {
        Users currentUser = currentUserService.getUserFromPrincipal(principal);
        return chatMessageService.sendMessage(currentUser, request);
    }

    @MessageMapping("/chat.read")
    public ReadReceiptEvent read(ReadReceiptRequest request, Principal principal) {
        Users currentUser = currentUserService.getUserFromPrincipal(principal);
        return chatMessageService.markRead(currentUser, request.getRoomId(), request.getMessageIds());
    }
}
