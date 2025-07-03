package com.chatapp.service.impl;

import com.chatapp.dao.constants.AppUserStatus;
import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.model.AppUserDO;
import com.chatapp.dao.model.LoginUserDO;
import com.chatapp.dao.service.AppUserRepoService;
import com.chatapp.dao.service.LoginUserRepoService;
import com.chatapp.exception.ChatAppException;
import com.chatapp.exception.ErrorCodes;
import com.chatapp.payload.request.UserRegisterRequest;
import com.chatapp.payload.response.UserHistoryChatsResponse;
import com.chatapp.payload.response.UserListChatsResponse;
import com.chatapp.payload.response.UserRegisterResponse;
import com.chatapp.service.ChatService;
import com.chatapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private LoginUserRepoService loginUserRepoService;

    @Autowired
    private AppUserRepoService appUserRepoService;

    @Autowired
    private ChatService chatService;

    @Override
    public UserRegisterResponse register(@NonNull UserRegisterRequest registerRequest) throws Exception {
        LoginUserDO loginUserDO = loginUserRepoService.getAppUserById(registerRequest.getSessionId());
        if (loginUserDO == null || loginUserDO.getStatus() != LoginUserStatus.OTP_VERIFIED) {
            throw new ChatAppException("invalid session id, try login again", ErrorCodes.INVALID_SESSION, HttpStatus.UNAUTHORIZED);
        }

        AppUserDO appUserDO = appUserRepoService.getAppUserByPhone(loginUserDO.getPhone());
        if (appUserDO == null) {
            appUserDO = new AppUserDO();
            appUserDO.setPhone(loginUserDO.getPhone());
            appUserDO.setEmail(registerRequest.getEmail());
            appUserDO.setStatus(AppUserStatus.ACTIVE);
            appUserDO.setAbout(registerRequest.getAbout());
            appUserDO.setName(registerRequest.getName());
            appUserDO.getInfo().putAll(registerRequest.getInfos());
        } else {
            if (registerRequest.getAbout() != null && !registerRequest.getAbout().trim().isEmpty())
                appUserDO.setAbout(registerRequest.getAbout());

            if (!registerRequest.getEmail().trim().isEmpty())
                appUserDO.setEmail(registerRequest.getEmail());
        }

        appUserDO = appUserRepoService.save(appUserDO);
        return makeUserRegisterResponse(appUserDO);
    }

    @Override
    public UserRegisterResponse checkUser(@NonNull UUID userId) throws Exception {
        AppUserDO appUserDO = appUserRepoService.getAppUserById(userId);
        if (appUserDO == null || appUserDO.getStatus() != AppUserStatus.ACTIVE) {
            throw new ChatAppException("invalid user id", ErrorCodes.INVALID_USER);
        }

        return makeUserRegisterResponse(appUserDO);
    }

    @Override
    public UserRegisterResponse findUser(@NonNull UUID userId, UUID otherUserId, String otherUserPhone) throws Exception {
        AppUserDO appUserDO = appUserRepoService.getAppUserById(userId);
        if (appUserDO == null || appUserDO.getStatus() != AppUserStatus.ACTIVE) {
            throw new ChatAppException("invalid user id", ErrorCodes.INVALID_USER);
        }

        if (otherUserId == null && (otherUserPhone == null || otherUserPhone.isEmpty())) {
            throw new ChatAppException("no valid information given", ErrorCodes.EMPTY_REQUEST);
        }

        AppUserDO appOtherUserDO;
        if (otherUserId != null) appOtherUserDO = appUserRepoService.getAppUserById(otherUserId);
        else appOtherUserDO = appUserRepoService.getAppUserByPhone(otherUserPhone);

        if (appOtherUserDO == null) {
            throw new ChatAppException("user not found", ErrorCodes.NOT_FOUND_USER);
        }

        return makeUserRegisterResponse(appOtherUserDO);
    }

    @Override
    public List<UserListChatsResponse> listChats(@NonNull UUID userId) throws Exception {
        return chatService.fetchListChats(userId);
    }

    @Override
    public List<UserHistoryChatsResponse> historyChats(@NonNull UUID currentUserId, @NonNull UUID otherUserId) {
        return chatService.fetchListChats(currentUserId, otherUserId);
    }

    UserRegisterResponse makeUserRegisterResponse(@NonNull AppUserDO appUserDO) {
        UserRegisterResponse response = new UserRegisterResponse();
        response.setStatus(appUserDO.getStatus().toString());
        response.setUserId(appUserDO.getUserId());
        response.setEmail(appUserDO.getEmail());
        response.setPhone(appUserDO.getPhone());
        response.setName(appUserDO.getName());
        response.setAbout(appUserDO.getAbout());
        return response;
    }
}
