package com.chatapp.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ChatAppException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final HttpStatus status;

    public ChatAppException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ChatAppException(String message, ErrorCodes errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}

