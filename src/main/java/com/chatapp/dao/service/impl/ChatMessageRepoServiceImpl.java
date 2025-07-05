package com.chatapp.dao.service.impl;

import com.chatapp.dao.model.ChatMessage;
import com.chatapp.dao.repository.ChatMessageRepository;
import com.chatapp.dao.service.ChatMessageRepoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageRepoServiceImpl implements ChatMessageRepoService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage save(@NonNull ChatMessage newMsg) {
        return chatMessageRepository.save(newMsg);
    }

    @Override
    public ChatMessage getById(@NonNull String id) {
        return chatMessageRepository.findById(id).orElse(null);
    }

    @Override
    public List<ChatMessage> getByIds(@NonNull List<String> ids) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessageRepository.findAllById(ids).forEach(chatMessages::add);
        return chatMessages;
    }
}
