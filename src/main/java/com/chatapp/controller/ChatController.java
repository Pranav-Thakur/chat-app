package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody Message message) {
        chatService.sendMessage(message);
    }

    @GetMapping("/history/{chatId}")
    public List<Message> getChatHistory(@PathVariable String chatId) {
        return chatService.getChatHistory(chatId);
    }
}
