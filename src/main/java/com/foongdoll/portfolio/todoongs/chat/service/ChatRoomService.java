package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatRoomSummaryResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.CreateDmRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.CreateGroupRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.RoomMemberResponse;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatMessage;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoom;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoomMember;
import com.foongdoll.portfolio.todoongs.chat.model.ChatRoomType;
import com.foongdoll.portfolio.todoongs.chat.model.RoomRole;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatMessageRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatRoomMemberRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public ChatRoomSummaryResponse createDmRoom(Users currentUser, CreateDmRequest request) {
        Users friend = usersRepository.findById(request.getFriendId())
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        ChatRoom room = chatRoomRepository
                .findDmRoom(ChatRoomType.DM, currentUser.getPk(), friend.getPk())
                .orElseGet(() -> {
                    ChatRoom created = new ChatRoom();
                    created.setType(ChatRoomType.DM);
                    created.setCreatedBy(currentUser);
                    ChatRoom saved = chatRoomRepository.save(created);

                    ChatRoomMember me = new ChatRoomMember();
                    me.setRoom(saved);
                    me.setUser(currentUser);
                    me.setRole(RoomRole.MEMBER);
                    chatRoomMemberRepository.save(me);

                    ChatRoomMember other = new ChatRoomMember();
                    other.setRoom(saved);
                    other.setUser(friend);
                    other.setRole(RoomRole.MEMBER);
                    chatRoomMemberRepository.save(other);

                    return saved;
                });

        return toSummary(currentUser, room);
    }

    @Transactional
    public ChatRoomSummaryResponse createGroupRoom(Users currentUser, CreateGroupRequest request) {
        ChatRoom room = new ChatRoom();
        room.setType(ChatRoomType.GROUP);
        room.setName(request.getName());
        room.setCreatedBy(currentUser);
        ChatRoom saved = chatRoomRepository.save(room);

        ChatRoomMember ownerMember = new ChatRoomMember();
        ownerMember.setRoom(saved);
        ownerMember.setUser(currentUser);
        ownerMember.setRole(RoomRole.OWNER);
        chatRoomMemberRepository.save(ownerMember);

        if (request.getMemberIds() != null) {
            for (String memberId : request.getMemberIds()) {
                if (memberId == null || memberId.isBlank() || memberId.equals(currentUser.getPk())) {
                    continue;
                }
                Users member = usersRepository.findById(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("Member not found"));
                ChatRoomMember roomMember = new ChatRoomMember();
                roomMember.setRoom(saved);
                roomMember.setUser(member);
                roomMember.setRole(RoomRole.MEMBER);
                chatRoomMemberRepository.save(roomMember);
            }
        }

        return toSummary(currentUser, saved);
    }

    public List<ChatRoomSummaryResponse> listRooms(Users currentUser) {
        return chatRoomRepository.findRoomsForUser(currentUser.getPk()).stream()
                .map(room -> toSummary(currentUser, room))
                .toList();
    }

    @Transactional
    public void setPinned(Users currentUser, String roomId, boolean pinned) {
        ChatRoomMember member = chatRoomMemberRepository.findByRoom_PkAndUser_Pk(roomId, currentUser.getPk())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        member.setPinned(pinned);
        chatRoomMemberRepository.save(member);
    }

    @Transactional
    public void setNotificationsEnabled(Users currentUser, String roomId, boolean enabled) {
        ChatRoomMember member = chatRoomMemberRepository.findByRoom_PkAndUser_Pk(roomId, currentUser.getPk())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        member.setNotificationsEnabled(enabled);
        chatRoomMemberRepository.save(member);
    }

    public List<RoomMemberResponse> listMembers(String roomId) {
        return chatRoomMemberRepository.findActiveByRoom(roomId).stream()
                .map(member -> RoomMemberResponse.builder()
                        .userId(member.getUser().getPk())
                        .name(member.getUser().getName())
                        .email(member.getUser().getEmail())
                        .role(member.getRole())
                        .build())
                .toList();
    }

    private ChatRoomSummaryResponse toSummary(Users currentUser, ChatRoom room) {
        ChatRoomMember me = chatRoomMemberRepository.findByRoom_PkAndUser_Pk(room.getPk(), currentUser.getPk())
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

        ChatMessage lastMessage = chatMessageRepository.findTopByRoom_PkOrderByCreatedDesc(room.getPk())
                .orElse(null);

        int unreadCount = 0;
        LocalDateTime lastReadAt = me.getLastReadAt();
        if (lastReadAt != null) {
            unreadCount = (int) chatMessageRepository.countByRoom_PkAndCreatedAfterAndSender_PkNot(room.getPk(), lastReadAt, currentUser.getPk());
        } else {
            unreadCount = (int) chatMessageRepository.countByRoom_PkAndSender_PkNot(room.getPk(), currentUser.getPk());
        }

        String lastMessagePreview = null;
        if (lastMessage != null) {
            lastMessagePreview = lastMessage.getContent();
            if (lastMessagePreview == null || lastMessagePreview.isBlank()) {
                lastMessagePreview = lastMessage.getType().name();
            }
        }

        return ChatRoomSummaryResponse.builder()
                .roomId(room.getPk())
                .name(room.getName())
                .type(room.getType())
                .pinned(me.isPinned())
                .notificationsEnabled(me.isNotificationsEnabled())
                .lastMessage(lastMessagePreview)
                .lastMessageType(lastMessage == null ? null : lastMessage.getType().name())
                .lastMessageAt(lastMessage == null ? null : lastMessage.getCreated())
                .unreadCount(unreadCount)
                .members(listMembers(room.getPk()))
                .build();
    }
}
