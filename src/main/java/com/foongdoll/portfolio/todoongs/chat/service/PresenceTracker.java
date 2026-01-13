package com.foongdoll.portfolio.todoongs.chat.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceTracker {

    private final Set<String> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void markOnline(String userId) {
        if (userId != null) {
            onlineUsers.add(userId);
        }
    }

    public void markOffline(String userId) {
        if (userId != null) {
            onlineUsers.remove(userId);
        }
    }

    public boolean isOnline(String userId) {
        return userId != null && onlineUsers.contains(userId);
    }
}
