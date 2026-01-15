package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, String> {

    Optional<ChatRoomMember> findByRoom_PkAndUser_Pk(String roomId, String userId);

    @Query("""
            select m from ChatRoomMember m
            where m.user.pk = :userId and m.active = true
            """)
    List<ChatRoomMember> findActiveByUser(@Param("userId") String userId);

    @Query("""
    select m
    from ChatRoomMember m
    join fetch m.user
    where m.room.pk = :roomId and m.active = true
    """)
    List<ChatRoomMember> findActiveByRoom(@Param("roomId") String roomId);
}
