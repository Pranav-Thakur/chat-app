package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.ChatService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@NonNull Message message) {
        message = chatService.saveMessage(message);
        // Then send to specific receiver topic
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), message);
    }

    @MessageMapping("/chat.delivered")
    public void handleDeliveryAck(@NonNull Message message) {
        message = chatService.markMessageAsDelivered(message);

        // Optionally notify sender over STOMP
        //Message message = messageService.getMessageById(ack.getMessageId());
        //messagingTemplate.convertAndSendToUser(message.getSenderId(), "/queue/message-status", new MessageStatusUpdate(message.getId(), "DELIVERED"));
    }
}
