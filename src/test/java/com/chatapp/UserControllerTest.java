package com.chatapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {
        String userJson = "{" +
                "  \"senderId\": \"user123\",\n" +
                "  \"receiverId\": \"user456\",\n" +
                "  \"content\": \"Hey there, Pro!\",\n" +
                "  \"timestamp\": 1724955600000,\n" +
                "  \"isRead\": false\n" +
                "}";

        mockMvc.perform(post("/api/v1/chat/send")
                        .contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isOk());
               // .andExpect(jsonPath("$.senderId").value("user123"))
               // .andExpect(jsonPath("$.receiverId").value("user456"));
    }
}
