package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.ChatRoomType;
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
public class ChatRoomSummaryResponse {
    private String roomId;
    private String name;
    private ChatRoomType type;
    private boolean pinned;
    private boolean notificationsEnabled;
    private String lastMessage;
    private String lastMessageType;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
    private List<RoomMemberResponse> members;
}
