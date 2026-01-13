package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoom;
import com.foongdoll.portfolio.todoongs.chat.model.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query("""
            select r from ChatRoom r
            where r.type = :type
              and r.pk in (
                select m.room.pk from ChatRoomMember m
                where m.user.pk in (:userId, :friendId) and m.active = true
                group by m.room.pk
                having count(m.room.pk) = 2
              )
            """)
    Optional<ChatRoom> findDmRoom(@Param("type") ChatRoomType type, @Param("userId") String userId, @Param("friendId") String friendId);

    @Query("""
            select r from ChatRoom r
            where r.pk in (
              select m.room.pk from ChatRoomMember m
              where m.user.pk = :userId and m.active = true
            )
            """)
    List<ChatRoom> findRoomsForUser(@Param("userId") String userId);
}
