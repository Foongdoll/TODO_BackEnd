package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.chat.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserSearchController {

    private final UsersRepository usersRepository;

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(@RequestParam("keyword") String keyword) {
        String trimmed = keyword == null ? "" : keyword.trim();
        if (trimmed.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(
                usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(trimmed, trimmed).stream()
                        .map(user -> UserSummaryResponse.builder()
                                .userId(user.getPk())
                                .email(user.getEmail())
                                .name(user.getName())
                                .build())
                        .toList()
        );
    }
}
