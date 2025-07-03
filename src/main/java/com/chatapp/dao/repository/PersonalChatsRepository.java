package com.chatapp.dao.repository;

import com.chatapp.dao.model.PersonalChats;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalChatsRepository extends MongoRepository<PersonalChats, String> {
    Optional<PersonalChats> findBySenderIdAndReceiverId(@NonNull String senderId, @NonNull String receiverId);
    Optional<List<PersonalChats>> findBySenderIdOrReceiverId(@NonNull String senderId, @NonNull String receiverId);
}

