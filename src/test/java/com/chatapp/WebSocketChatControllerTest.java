package com.chatapp;

import com.chatapp.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class WebSocketChatControllerTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession session;

    @BeforeEach
    void setup() throws Exception {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        session = stompClient.connect(
                "ws://localhost:" + port + "/ws",
                new StompSessionHandlerAdapter() {}
        ).get(1, TimeUnit.SECONDS);
    }

    @Test
    void shouldSendAndReceiveWebSocketMessage() throws Exception {
        BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();

        session.subscribe("/topic/messages", new StompFrameHandler() {
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((Message) payload);
            }
        });

        Message msg = new Message();
        //msg.setSenderId("user123");
        //msg.setReceiverId("user456");
        msg.setContent("Hey there, Pro!");
        msg.setTimestamp(System.currentTimeMillis());
        msg.setRead(false);

        session.send("/app/chat.send", msg);

        Message response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);
        assertEquals("user123", response.getSenderId());
        assertEquals("Hey there, Pro!", response.getContent());
    }
}
