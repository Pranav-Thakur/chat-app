package com.chatapp.payload.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class UserRegisterRequest {
    @NotNull(message = "sessionId is required")
    private UUID sessionId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    private String email;
    private String about;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Map<String, Object> infos = new HashMap<>();
}
