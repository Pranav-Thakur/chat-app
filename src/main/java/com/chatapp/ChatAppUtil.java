package com.chatapp;

import com.chatapp.exception.ChatAppException;
import com.chatapp.exception.ErrorCodes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

public class ChatAppUtil {

    public static final String JWT_TOKEN = "jwtToken";

    public static String randomOtp(int otpLength) {
        int start = (int) Math.pow(10, otpLength-1);
        int bound = (int) Math.pow(10, otpLength) - start;
        return String.valueOf(new Random().nextInt(bound) + start);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds, boolean httpOnly) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(true) // only HTTPS in prod
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String extractTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static Map<String, Object> errorBody(String message, HttpStatus status) {
        return errorBody(message, ErrorCodes.INTERNAL_SERVER_ERROR, status);
    }

    public static Map<String, Object> errorBody(String message, ErrorCodes errorCode, HttpStatus status) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "errorCode", errorCode.getCode(),
                "message", message
        );
    }

    public static Map<String, Object> errorBody(ChatAppException exception) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", exception.getStatus().value(),
                "errorCode", exception.getErrorCode().getCode(),
                "message", exception.getMessage()
        );
    }
}
