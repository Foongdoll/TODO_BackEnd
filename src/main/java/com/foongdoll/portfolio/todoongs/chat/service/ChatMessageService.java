package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatAttachmentResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageRequest;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatMessageResponse;
import com.foongdoll.portfolio.todoongs.chat.dto.ChatNotificationEvent;
import com.foongdoll.portfolio.todoongs.chat.dto.ReadReceiptEvent;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatAttachment;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatMessage;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatMessageRead;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoom;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoomMember;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatAttachmentRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatMessageReadRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatMessageRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatRoomMemberRepository;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatAttachmentRepository chatAttachmentRepository;
    private final ChatMessageReadRepository chatMessageReadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponse sendMessage(Users sender, ChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        ensureMember(room, sender);

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(sender);
        message.setType(request.getType());
        message.setContent(request.getContent());
        message.setPayload(request.getPayload());

        ChatMessage saved = chatMessageRepository.save(message);

        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            for (String attachmentId : request.getAttachmentIds()) {
                ChatAttachment attachment = chatAttachmentRepository.findById(attachmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
                attachment.setMessage(saved);
                chatAttachmentRepository.save(attachment);
            }
        }

        ChatMessageResponse response = toMessageResponse(saved, Map.of());
        messagingTemplate.convertAndSend("/topic/rooms/" + room.getPk(), response);
        publishNotifications(room, saved);
        return response;
    }

    public List<ChatMessageResponse> listMessages(Users user, String roomId, int limit) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        ensureMember(room, user);

        List<ChatMessage> messages = chatMessageRepository.findRecent(roomId, null);
        if (limit > 0 && messages.size() > limit) {
            messages = messages.subList(0, limit);
        }

        Set<String> messageIds = new HashSet<>();
        for (ChatMessage message : messages) {
            messageIds.add(message.getPk());
        }

        Map<String, Set<String>> readMap = new HashMap<>();
        if (!messageIds.isEmpty()) {
            List<ChatMessageRead> reads = chatMessageReadRepository.findByMessageIds(messageIds);
            for (ChatMessageRead read : reads) {
                readMap.computeIfAbsent(read.getMessage().getPk(), key -> new HashSet<>())
                        .add(read.getReader().getPk());
            }
        }

        return messages.stream()
                .map(message -> toMessageResponse(message, readMap))
                .toList();
    }

    @Transactional
    public ReadReceiptEvent markRead(Users user, String roomId, List<String> messageIds) {
        ChatRoomMember member = chatRoomMemberRepository.findByRoom_PkAndUser_Pk(roomId, user.getPk())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        LocalDateTime now = LocalDateTime.now();
        member.setLastReadAt(now);
        chatRoomMemberRepository.save(member);

        if (messageIds != null) {
            for (String messageId : messageIds) {
                if (chatMessageReadRepository.findByMessage_PkAndReader_Pk(messageId, user.getPk()).isPresent()) {
                    continue;
                }
                ChatMessage message = chatMessageRepository.findById(messageId)
                        .orElseThrow(() -> new IllegalArgumentException("Message not found"));
                ChatMessageRead read = new ChatMessageRead();
                read.setMessage(message);
                read.setReader(user);
                read.setReadAt(now);
                chatMessageReadRepository.save(read);
            }
        }

        ReadReceiptEvent event = ReadReceiptEvent.builder()
                .roomId(roomId)
                .readerId(user.getPk())
                .messageIds(messageIds)
                .readAt(now)
                .build();
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/reads", event);
        return event;
    }

    private void publishNotifications(ChatRoom room, ChatMessage message) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findActiveByRoom(room.getPk());
        for (ChatRoomMember member : members) {
            Users user = member.getUser();
            if (user.getPk().equals(message.getSender().getPk())) {
                continue;
            }
            if (!member.isNotificationsEnabled() || !user.isNotificationsEnabled()) {
                continue;
            }

            String preview = message.getContent();
            if (preview == null || preview.isBlank()) {
                preview = message.getType().name();
            }

            ChatNotificationEvent event = ChatNotificationEvent.builder()
                    .roomId(room.getPk())
                    .messageId(message.getPk())
                    .senderName(message.getSender().getName())
                    .preview(preview)
                    .type(message.getType())
                    .createdAt(message.getCreated())
                    .build();
            messagingTemplate.convertAndSend("/topic/notifications/" + user.getPk(), event);
        }
    }

    private void ensureMember(ChatRoom room, Users user) {
        chatRoomMemberRepository.findByRoom_PkAndUser_Pk(room.getPk(), user.getPk())
                .orElseThrow(() -> new IllegalArgumentException("Not a member of the room"));
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message, Map<String, Set<String>> readMap) {
        List<ChatAttachmentResponse> attachments = chatAttachmentRepository.findByMessage_Pk(message.getPk()).stream()
                .map(attachment -> ChatAttachmentResponse.builder()
                        .attachmentId(attachment.getPk())
                        .type(attachment.getType())
                        .name(attachment.getOriginalName())
                        .url(attachment.getUrl())
                        .mime(attachment.getMime())
                        .size(attachment.getSize())
                        .build())
                .toList();

        return ChatMessageResponse.builder()
                .messageId(message.getPk())
                .roomId(message.getRoom().getPk())
                .senderId(message.getSender().getPk())
                .senderName(message.getSender().getName())
                .type(message.getType())
                .content(message.getContent())
                .payload(message.getPayload())
                .attachments(attachments)
                .readUserIds(readMap.getOrDefault(message.getPk(), Set.of()))
                .createdAt(message.getCreated())
                .build();
    }
}
