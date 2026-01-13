package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.chat.dto.FriendResponse;
import com.foongdoll.portfolio.todoongs.chat.entity.FriendRelation;
import com.foongdoll.portfolio.todoongs.chat.model.FriendStatus;
import com.foongdoll.portfolio.todoongs.chat.repository.FriendRelationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRelationRepository friendRelationRepository;
    private final UsersRepository usersRepository;
    private final PresenceTracker presenceTracker;

    public List<FriendResponse> listFriends(Users owner) {
        return friendRelationRepository.findByOwner(owner).stream()
                .map(relation -> FriendResponse.builder()
                        .userId(relation.getFriend().getPk())
                        .email(relation.getFriend().getEmail())
                        .name(relation.getFriend().getName())
                        .status(relation.getStatus())
                        .online(presenceTracker.isOnline(relation.getFriend().getPk()))
                        .build())
                .toList();
    }

    @Transactional
    public FriendResponse addFriend(Users owner, String friendId) {
        Users friend = usersRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        upsertRelation(owner, friend, FriendStatus.ACTIVE);
        upsertRelation(friend, owner, FriendStatus.ACTIVE);

        return FriendResponse.builder()
                .userId(friend.getPk())
                .email(friend.getEmail())
                .name(friend.getName())
                .status(FriendStatus.ACTIVE)
                .online(presenceTracker.isOnline(friend.getPk()))
                .build();
    }

    @Transactional
    public void removeFriend(Users owner, String friendId) {
        Users friend = usersRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        friendRelationRepository.findByOwnerAndFriend(owner, friend)
                .ifPresent(friendRelationRepository::delete);
        friendRelationRepository.findByOwnerAndFriend(friend, owner)
                .ifPresent(friendRelationRepository::delete);
    }

    @Transactional
    public void blockFriend(Users owner, String friendId) {
        Users friend = usersRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        upsertRelation(owner, friend, FriendStatus.BLOCKED);
    }

    private void upsertRelation(Users owner, Users friend, FriendStatus status) {
        FriendRelation relation = friendRelationRepository.findByOwnerAndFriend(owner, friend)
                .orElseGet(() -> {
                    FriendRelation created = new FriendRelation();
                    created.setOwner(owner);
                    created.setFriend(friend);
                    return created;
                });

        relation.setStatus(status);
        friendRelationRepository.save(relation);
    }
}
