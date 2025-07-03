package com.chatapp.dao.service;

import com.chatapp.dao.model.PersonalChats;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface PersonalChatsRepoService {
    PersonalChats findById(@NonNull String id);
    PersonalChats save(@NonNull PersonalChats personalChats);
    PersonalChats findBySenderIdAndReceiverId(@NonNull UUID senderId, @NonNull UUID receiverId);
    List<PersonalChats> findChatsBySenderIdOrReceiverId(@NonNull UUID senderId, @NonNull UUID receiverId);
}
