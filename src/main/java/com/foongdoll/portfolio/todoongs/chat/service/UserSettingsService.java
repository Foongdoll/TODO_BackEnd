package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UsersRepository usersRepository;

    @Transactional
    public Users updateNotificationSetting(Users user, boolean enabled) {
        user.setNotificationsEnabled(enabled);
        return usersRepository.save(user);
    }
}
