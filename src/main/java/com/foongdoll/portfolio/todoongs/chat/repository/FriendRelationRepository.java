package com.foongdoll.portfolio.todoongs.chat.repository;

import com.foongdoll.portfolio.todoongs.chat.entity.FriendRelation;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.chat.model.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, String> {

    Optional<FriendRelation> findByOwner_PkAndFriend_Pk(String ownerId, String friendId);

    List<FriendRelation> findByOwner(Users owner);

    Optional<FriendRelation> findByOwnerAndFriend(Users owner, Users friend);

    List<FriendRelation> findByFriendAndStatus(Users friend, FriendStatus status);

    @Query("""
            select f from FriendRelation f
            where f.owner.pk = :ownerId and f.status = :status
            """)
    List<FriendRelation> findByOwnerAndStatus(@Param("ownerId") String ownerId, @Param("status") FriendStatus status);
}
