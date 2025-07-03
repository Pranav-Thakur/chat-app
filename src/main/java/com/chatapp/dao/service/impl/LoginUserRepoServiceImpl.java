package com.chatapp.dao.service.impl;


import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.model.LoginUserDO;
import com.chatapp.dao.repository.LoginUserRepository;
import com.chatapp.dao.service.LoginUserRepoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoginUserRepoServiceImpl implements LoginUserRepoService {

    @Autowired
    private LoginUserRepository appUserRepository;

    @Override
    public LoginUserDO getAppUserById(@NonNull UUID id) {
        return appUserRepository.findById(id).orElse(null);
    }

    @Override
    public LoginUserDO save(@NonNull LoginUserDO appUserDO) {
        return appUserRepository.save(appUserDO);
    }

    @Override
    public boolean existsBySessionIdAndStatus(@NonNull UUID sessionId, @NonNull LoginUserStatus status) {
        return appUserRepository.existsBySessionIdAndStatus(sessionId, status);
    }

    @Override
    public List<LoginUserDO> getAppUserByPhoneAndStatus(@NonNull String phone, @NonNull LoginUserStatus status, int limit) {
        Pageable lmt = PageRequest.of(0, limit);
        return appUserRepository.findByPhoneAndStatus(phone, status, lmt).orElse(new ArrayList<>());
    }
}
