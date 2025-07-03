package com.chatapp.service;

import lombok.NonNull;

public interface SmsSender {
    String sendOtp(@NonNull String toPhoneNumber, @NonNull String otp);
}
