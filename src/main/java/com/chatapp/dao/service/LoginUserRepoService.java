package com.chatapp.dao.service;

import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.model.LoginUserDO;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface LoginUserRepoService {
    LoginUserDO getAppUserById(@NonNull UUID sessionId);
    LoginUserDO save(@NonNull LoginUserDO userDO);
    boolean existsBySessionIdAndStatus(@NonNull UUID sessionId, @NonNull LoginUserStatus status);
    List<LoginUserDO> getAppUserByPhoneAndStatus(@NonNull String phone, @NonNull LoginUserStatus loginUserStatus, int limit);
}