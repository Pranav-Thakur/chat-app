package com.chatapp.service;

import com.chatapp.payload.request.UserRegisterRequest;
import com.chatapp.payload.response.UserHistoryChatsResponse;
import com.chatapp.payload.response.UserListChatsResponse;
import com.chatapp.payload.response.UserRegisterResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserRegisterResponse register(@NonNull UserRegisterRequest registerRequest) throws Exception;
    UserRegisterResponse checkUser(@NonNull UUID userId) throws Exception;
    UserRegisterResponse findUser(@NonNull UUID userId, UUID otherUserId, String otherUserPhone) throws Exception;
    List<UserListChatsResponse> listChats(@NonNull UUID userId) throws Exception;
    List<UserHistoryChatsResponse> historyChats(@NonNull UUID currentUserId, @NonNull UUID otherUserId);
}
