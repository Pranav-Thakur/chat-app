package com.chatapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getRequestURI();

        // 🛑 Bypass logging for streaming endpoints
        if (path.startsWith("/api/v1/user/consume") || path.startsWith("/health")) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long start = System.currentTimeMillis();
        chain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - start;

        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("timestamp", LocalDateTime.now().toString());
        logData.put("method", wrappedRequest.getMethod());
        logData.put("path", wrappedRequest.getRequestURI());
        logData.put("durationMs", duration);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("headers", getHeadersMap(wrappedRequest));
        requestMap.put("body", parseJsonOrReturnString(getRequestBody(wrappedRequest)));
        logData.put("request", requestMap);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", wrappedResponse.getStatus());
        responseMap.put("body", parseJsonOrReturnString(getResponseBody(wrappedResponse)));
        logData.put("response", responseMap);

        logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logData));
        wrappedResponse.copyBodyToResponse();
    }

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private Object parseJsonOrReturnString(String body) {
        if (body == null || body.trim().isEmpty()) return "[empty]";
        try {
            return mapper.readValue(body, Object.class);
        } catch (JsonProcessingException e) {
            return body; // fallback to raw string
        }
    }
}
