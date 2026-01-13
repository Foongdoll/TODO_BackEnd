package com.foongdoll.portfolio.todoongs.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateGroupRequest {
    private String name;
    private List<String> memberIds;
}
