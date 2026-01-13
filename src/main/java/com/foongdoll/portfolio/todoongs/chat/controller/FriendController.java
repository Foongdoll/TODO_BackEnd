package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.FriendResponse;
import com.foongdoll.portfolio.todoongs.chat.service.CurrentUserService;
import com.foongdoll.portfolio.todoongs.chat.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final CurrentUserService currentUserService;
    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponse>> listFriends() {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(friendService.listFriends(currentUser));
    }

    @PostMapping
    public ResponseEntity<FriendResponse> addFriend(@RequestParam("friendId") String friendId) {
        Users currentUser = currentUserService.getCurrentUser();
        return ResponseEntity.ok(friendService.addFriend(currentUser, friendId));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable String friendId) {
        Users currentUser = currentUserService.getCurrentUser();
        friendService.removeFriend(currentUser, friendId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{friendId}/block")
    public ResponseEntity<Void> blockFriend(@PathVariable String friendId) {
        Users currentUser = currentUserService.getCurrentUser();
        friendService.blockFriend(currentUser, friendId);
        return ResponseEntity.noContent().build();
    }
}
