package com.foongdoll.portfolio.todoongs.chat.dto;

import com.foongdoll.portfolio.todoongs.chat.model.RoomRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberResponse {
    private String userId;
    private String name;
    private String email;
    private RoomRole role;
}
