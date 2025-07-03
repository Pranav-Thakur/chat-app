package com.chatapp.dao.service;

import com.chatapp.dao.constants.AppUserStatus;
import com.chatapp.dao.model.AppUserDO;
import lombok.NonNull;

import java.util.UUID;

public interface AppUserRepoService {
    AppUserDO getAppUserById(@NonNull UUID userId);
    AppUserDO save(@NonNull AppUserDO userDO);
    boolean existsByUserIdAndStatus(@NonNull UUID userId, @NonNull AppUserStatus status);
    AppUserDO getAppUserByPhone(@NonNull String phone);
}
