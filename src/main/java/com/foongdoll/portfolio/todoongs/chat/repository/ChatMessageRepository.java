package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    @Query("""
            select m from ChatMessage m
            where m.room.pk = :roomId
              and (:before is null or m.created < :before)
            order by m.created desc
            """)
    List<ChatMessage> findRecent(@Param("roomId") String roomId, @Param("before") LocalDateTime before);

    @Query("""
            select m from ChatMessage m
            where m.room.pk = :roomId
            order by m.created desc
            """)
    List<ChatMessage> findLatest(@Param("roomId") String roomId);

    @Query("""
            select m from ChatMessage m
            where m.room.pk = :roomId
            order by m.created desc
            """)
    List<ChatMessage> findLatestOne(@Param("roomId") String roomId);

    Optional<ChatMessage> findTopByRoom_PkOrderByCreatedDesc(String roomId);

    long countByRoom_PkAndCreatedAfterAndSender_PkNot(String roomId, LocalDateTime createdAfter, String senderId);

    long countByRoom_PkAndSender_PkNot(String roomId, String senderId);
}
