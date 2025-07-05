package com.chatapp.dao.service;

import com.chatapp.dao.model.ChatMessage;
import lombok.NonNull;

import java.util.List;

public interface ChatMessageRepoService {
    ChatMessage save(@NonNull ChatMessage newMsg);
    ChatMessage getById(@NonNull String id);
    List<ChatMessage> getByIds(@NonNull List<String> ids);
}
