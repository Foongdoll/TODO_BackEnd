package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatRoomSummaryResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.CreateDmRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.CreateGroupRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.RoomPreferenceRequest;
import com.foongdoll.portfolio.todoongs.chat.service.ChatRoomService;
import com.foongdoll.portfolio.todoongs.chat.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final CurrentUserService currentUserService;
    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomSummaryResponse>> listRooms() {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatRoomService.listRooms(currentUser));
    }

    @PostMapping("/dm")
    public ResponseEntity<ChatRoomSummaryResponse> createDm(@RequestBody CreateDmRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatRoomService.createDmRoom(currentUser, request));
    }

    @PostMapping("/group")
    public ResponseEntity<ChatRoomSummaryResponse> createGroup(@RequestBody CreateGroupRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(chatRoomService.createGroupRoom(currentUser, request));
    }

    @PostMapping("/{roomId}/pin")
    public ResponseEntity<Void> pinRoom(@PathVariable String roomId, @RequestBody RoomPreferenceRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        chatRoomService.setPinned(currentUser, roomId, request.isEnabled());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/notifications")
    public ResponseEntity<Void> toggleRoomNotifications(@PathVariable String roomId, @RequestBody RoomPreferenceRequest request) {
        Users currentUser = currentUserService.getCurrentUser();
        chatRoomService.setNotificationsEnabled(currentUser, roomId, request.isEnabled());
        return ResponseEntity.noContent().build();
    }
}
