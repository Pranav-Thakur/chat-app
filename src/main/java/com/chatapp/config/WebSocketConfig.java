package com.chatapp.config;

import com.chatapp.ChatAppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UuidAuthenticationProvider uuidAuthProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        // You can put your JWT-based Principal logic here
                        attributes.put("request", request); // ✅ Store for later use
                        return super.determineUser(request, wsHandler, attributes);
                    }
                })
                .setAllowedOrigins("https://192.168.*", "https://localhost:8080", "https://chat-app-production-e971.up.railway.app");
    }

    public TaskScheduler messageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
                //.setTaskScheduler(messageBrokerTaskScheduler());
                //.setHeartbeatValue(new long[] {10000, 10000});
        config.setApplicationDestinationPrefixes("/app");
    }

    //@Override
    public void configureClientInboundChannelll(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // ✅ Extract from session attributes, not headers
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null) {
                        ServerHttpRequest request = (ServerHttpRequest) sessionAttributes.get("request");
                        if (request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

                            Cookie[] cookies = httpServletRequest.getCookies();
                            String jwt = Arrays.stream(cookies)
                                    .filter(c -> c.getName().equals(ChatAppUtil.JWT_TOKEN))
                                    .findFirst()
                                    .map(Cookie::getValue)
                                    .orElse(null);

                            if (jwt != null) {
                                Authentication auth = uuidAuthProvider.authenticate(
                                        new UuidAuthenticationToken(UUID.fromString(jwtUtil.extractSubject(jwt))));
                                accessor.setUser(auth);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }
}
