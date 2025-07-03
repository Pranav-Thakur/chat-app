package com.chatapp.payload.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import javax.validation.constraints.NotBlank;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuthLoginRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Device ID is required")
    private String deviceId;
    private String agent;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Map<String, Object> infos = new HashMap<>();
}
