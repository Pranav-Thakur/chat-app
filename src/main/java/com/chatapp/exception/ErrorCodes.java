package com.chatapp.exception;

public enum ErrorCodes {
    EMPTY_AUTH_TOKEN("AT-00", "empty auth token"),
    INVALID_AUTH_TOKEN("AT-01", "invalid auth token"),
    UNAUTH_AUTH_TOKEN("AT-02", "unauth auth token"),
    EXPIRY_AUTH_TOKEN("AT-03", "expired auth token"),
    INVALID_SESSION("AT-04", "session invalid"),
    EMPTY_REQUEST("RQ-00", "empty request"),
    INVALID_PHONE("RQ-01", "invalid phone"),
    EXPIERED_OTP("RQ-02", "otp expired"),
    ERROR_VALIDATE_OTP("RQ-03", "validate otp failed"),
    INVALID_USER("RQ-04", "user invalid"),
    NOT_FOUND_USER("RQ-05", "user not found"),
    INCORRECT_OTP("RQ-06", "otp incorrect"),
    INTERNAL_SERVER_ERROR("SE-00", "server down");

    private final String code;
    private final String msg;

    ErrorCodes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
