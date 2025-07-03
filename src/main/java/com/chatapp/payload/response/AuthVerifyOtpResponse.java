package com.chatapp.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthVerifyOtpResponse {

    @Schema(description = "session ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID sessionId;

    @Schema(description = "a message for client", required = true, example = "success done.")
    private String message;
    private String token;

    @Schema(description = "user id if phone present as user", required = false, example = "123e4567-e89b-12d3-a456-426614174000")
    private String userId;
}
