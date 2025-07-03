package com.chatapp.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class AuthVerifyOtpRequest {
    @NotNull(message = "sessionId is required")
    private UUID sessionId;

    @NotBlank(message = "otp is required")
    private String otp;

    @NotBlank(message = "deviceId is required")
    private String deviceId;
    private String agent;
}
