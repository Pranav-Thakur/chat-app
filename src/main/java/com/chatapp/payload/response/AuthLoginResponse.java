package com.chatapp.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthLoginResponse {

    @Schema(description = "session ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID sessionId;

    @Schema(description = "phone number", required = true, example = "1234567890")
    private String phoneNumber;

    @Schema(description = "a message for client", required = true, example = "success done.")
    private String message;
}
