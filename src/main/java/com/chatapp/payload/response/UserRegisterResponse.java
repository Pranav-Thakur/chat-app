package com.chatapp.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegisterResponse {

    @Schema(description = "user ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "email of user", required = true, example = "abc@def.com")
    private String email;

    @Schema(description = "phone number", required = true, example = "1234567890")
    private String phone;

    @Schema(description = "user name", required = true, example = "abc def")
    private String name;
    private String about;

    @Schema(description = "user status: ACTIVE, SUSPENDED, DELETED", required = true, example = "ACTIVE")
    private String status;
}
