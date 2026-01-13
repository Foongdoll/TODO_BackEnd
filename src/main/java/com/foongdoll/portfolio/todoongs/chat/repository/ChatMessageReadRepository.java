package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatMessageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface ChatMessageReadRepository extends JpaRepository<ChatMessageRead, String> {

    @Query("""
            select r from ChatMessageRead r
            where r.message.pk in :messageIds
            """)
    List<ChatMessageRead> findByMessageIds(@Param("messageIds") Set<String> messageIds);

    Optional<ChatMessageRead> findByMessage_PkAndReader_Pk(String messageId, String readerId);
}
