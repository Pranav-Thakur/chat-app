package com.chatapp.dao.service.impl;

import com.chatapp.dao.constants.AppUserStatus;
import com.chatapp.dao.model.AppUserDO;
import com.chatapp.dao.repository.AppUserRepository;
import com.chatapp.dao.service.AppUserRepoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AppUserRepoServiceImpl implements AppUserRepoService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public AppUserDO getAppUserById(@NonNull UUID userId) {
        return appUserRepository.findById(userId).orElse(null);
    }

    @Override
    public AppUserDO save(@NonNull AppUserDO userDO) {
        return appUserRepository.save(userDO);
    }

    @Override
    public boolean existsByUserIdAndStatus(@NonNull UUID userId, @NonNull AppUserStatus status) {
        return appUserRepository.existsByUserIdAndStatus(userId, status);
    }

    @Override
    public AppUserDO getAppUserByPhone(@NonNull String phone) {
        return appUserRepository.findByPhone(phone).orElse(null);
    }
}
