package com.chatapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class Message {
    private String messageId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private long timestamp;
    private long deliveryTimestamp;
    private long readTimestamp;

    @JsonProperty("isRead")
    private boolean read;
}
