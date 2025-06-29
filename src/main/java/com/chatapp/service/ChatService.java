package com.chatapp.service;

import com.chatapp.model.Message;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    private Map<String, List<Message>> chatStore = new HashMap<>();

    public void sendMessage(Message message) {
        chatStore.computeIfAbsent(message.getReceiverId(), k -> new ArrayList<>()).add(message);
    }

    public List<Message> getChatHistory(String chatId) {
        return chatStore.getOrDefault(chatId, new ArrayList<>());
    }
}
