package com.chatapp.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHistoryChatsResponse {

    @Schema(description = "sender ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID senderId;

    @Schema(description = "receiver name", required = true, example = "abc def")
    private String name;

    @Schema(description = "phone number", required = true, example = "1234567890")
    private String phone;

    @Schema(description = "message for user", required = true, example = "abc def")
    private String message;

    @Schema(description = "timestamp of last message", required = true, example = "1234567890322")
    private long timestamp;

    @Schema(description = "timestamp of last message", required = true, example = "1234567890322")
    private long deliveredTimestamp;

    @Schema(description = "timestamp of last message", required = true, example = "1234567890322")
    private long readTimestamp;

    @Schema(description = "unread messages count", required = true, example = "3")
    private boolean read;
}
