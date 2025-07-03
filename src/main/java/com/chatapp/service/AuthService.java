package com.chatapp.service;

import com.chatapp.payload.request.AuthLoginRequest;
import com.chatapp.payload.request.AuthVerifyOtpRequest;
import com.chatapp.payload.response.AuthLoginResponse;
import com.chatapp.payload.response.AuthVerifyOtpResponse;
import lombok.NonNull;

public interface AuthService {
    AuthLoginResponse generateOtp(@NonNull AuthLoginRequest loginRequest) throws Exception;
    AuthVerifyOtpResponse verifyOtp(@NonNull AuthVerifyOtpRequest req) throws Exception;
    AuthVerifyOtpResponse refreshToken(@NonNull String deviceId, @NonNull String jwtToken) throws Exception;
}
