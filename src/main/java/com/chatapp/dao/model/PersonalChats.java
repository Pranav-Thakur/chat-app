package com.chatapp.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "chats")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalChats {

    @Id
    private String id; // You can create a unique key like user1_user2
    private String senderId;
    private String receiverId;
    private List<String> messages = new ArrayList<>();
}