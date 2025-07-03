package com.chatapp.controller;

import com.chatapp.ChatAppUtil;
import com.chatapp.exception.ChatAppException;
import com.chatapp.exception.ErrorCodes;
import com.chatapp.payload.request.AuthLoginRequest;
import com.chatapp.payload.request.AuthVerifyOtpRequest;
import com.chatapp.payload.response.AuthLoginResponse;
import com.chatapp.payload.response.AuthVerifyOtpResponse;
import com.chatapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${chatapp.cookie.maxage.seconds}")
    private int cookieMaxAgeInSeconds;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> sendOtp(@Valid @RequestBody AuthLoginRequest loginRequest) throws Exception {
        AuthLoginResponse response = authService.generateOtp(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthVerifyOtpResponse> verifyOtp(@Valid @RequestBody AuthVerifyOtpRequest req, HttpServletResponse response) throws Exception {
        AuthVerifyOtpResponse resp = authService.verifyOtp(req);
        if (resp.getToken() != null && !resp.getToken().isEmpty()) {
            ChatAppUtil.addCookie(response, ChatAppUtil.JWT_TOKEN, resp.getToken(), cookieMaxAgeInSeconds, true);
            resp.setToken(null);
            return ResponseEntity.ok(resp);
        } else {
            // invalid cookie case, so clear cookie
            ChatAppUtil.addCookie(response, ChatAppUtil.JWT_TOKEN, "", 0, true);
            throw new ChatAppException("auth token empty", ErrorCodes.EMPTY_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthVerifyOtpResponse> refreshToken(@RequestParam String deviceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 🥠 Get JWT from Cookie
        String jwtToken = ChatAppUtil.extractTokenFromCookies(request.getCookies());
        if (jwtToken == null || jwtToken.isEmpty())
            throw new ChatAppException("auth token empty", ErrorCodes.EMPTY_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);

        try {
            AuthVerifyOtpResponse resp = authService.refreshToken(deviceId, jwtToken);
            if (resp.getToken() != null && !resp.getToken().isEmpty()) {
                ChatAppUtil.addCookie(response, ChatAppUtil.JWT_TOKEN, resp.getToken(), cookieMaxAgeInSeconds, true);
                resp.setToken(null);
                return ResponseEntity.ok(resp);
            }

            // if token is empty
            throw new ChatAppException("auth token empty", ErrorCodes.EMPTY_AUTH_TOKEN, HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            // handling it just to make sure in error case cookie clear
            if (e instanceof ChatAppException) {
                ChatAppException ex = (ChatAppException) e;
                if (ex.getErrorCode().getCode().startsWith("AT-")) { // auth token related errors
                    // clear cookie in case it is not positive case
                    ChatAppUtil.addCookie(response, ChatAppUtil.JWT_TOKEN, "", 0, true);
                }
            }

            throw e;
        }
    }
}
