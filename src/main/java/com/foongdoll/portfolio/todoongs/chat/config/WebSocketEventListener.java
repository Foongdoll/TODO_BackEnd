package com.foongdoll.portfolio.todoongs.chat.config;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.chat.dto.PresenceEvent;
import com.foongdoll.portfolio.todoongs.chat.entity.FriendRelation;
import com.foongdoll.portfolio.todoongs.chat.model.FriendStatus;
import com.foongdoll.portfolio.todoongs.chat.repository.FriendRelationRepository;
import com.foongdoll.portfolio.todoongs.chat.service.PresenceTracker;
import com.foongdoll.portfolio.todoongs.security.dto.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UsersRepository usersRepository;
    private final FriendRelationRepository friendRelationRepository;
    private final PresenceTracker presenceTracker;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = extractEmail(accessor);
        if (email == null) {
            return;
        }
        Optional<Users> user = usersRepository.findByEmail(email);
        if (user.isEmpty()) {
            return;
        }
        Users currentUser = user.get();
        presenceTracker.markOnline(currentUser.getPk());
        broadcastPresence(currentUser.getPk(), true);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = extractEmail(accessor);
        if (email == null) {
            return;
        }
        Optional<Users> user = usersRepository.findByEmail(email);
        if (user.isEmpty()) {
            return;
        }
        Users currentUser = user.get();
        presenceTracker.markOffline(currentUser.getPk());
        broadcastPresence(currentUser.getPk(), false);
    }

    private String extractEmail(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            return null;
        }
        Object principal = accessor.getUser();
        if (principal instanceof UserDetails details) {
            return details.getUsername();
        }
        if (principal instanceof org.springframework.security.core.userdetails.User details) {
            return details.getUsername();
        }
        return accessor.getUser().getName();
    }

    private void broadcastPresence(String userId, boolean online) {
        PresenceEvent event = PresenceEvent.builder()
                .userId(userId)
                .online(online)
                .at(LocalDateTime.now())
                .build();

        List<FriendRelation> relations = friendRelationRepository.findByOwnerAndStatus(userId, FriendStatus.ACTIVE);
        for (FriendRelation relation : relations) {
            messagingTemplate.convertAndSend("/topic/presence/" + relation.getFriend().getPk(), event);
        }
    }
}
