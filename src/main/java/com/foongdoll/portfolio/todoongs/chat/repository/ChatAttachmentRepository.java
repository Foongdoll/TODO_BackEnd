package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, String> {
    List<ChatAttachment> findByMessage_Pk(String messageId);
}
