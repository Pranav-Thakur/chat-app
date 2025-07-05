package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.payload.response.UserHistoryChatsResponse;
import com.chatapp.payload.response.UserListChatsResponse;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    Message saveMessage(@NonNull Message message);
    List<UserListChatsResponse> fetchListChats(@NonNull UUID userId);
    List<UserHistoryChatsResponse> fetchListChats(@NonNull UUID currentUserId, @NonNull UUID otherUserId);
    Message markMessageAsDelivered(@NonNull Message  message);
}
