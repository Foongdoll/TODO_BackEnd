package com.foongdoll.portfolio.todoongs.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptEvent {
    private String roomId;
    private String readerId;
    private List<String> messageIds;
    private LocalDateTime readAt;
}
