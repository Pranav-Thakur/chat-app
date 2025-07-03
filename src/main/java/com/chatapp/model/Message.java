package com.chatapp.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Message {
    private String messageId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private long timestamp;
    private boolean isRead;
}
