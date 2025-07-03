package com.chatapp.dao.service.impl;

import com.chatapp.dao.model.PersonalChats;
import com.chatapp.dao.repository.PersonalChatsRepository;
import com.chatapp.dao.service.PersonalChatsRepoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PersonalChatsRepoServiceImpl implements PersonalChatsRepoService {

    @Autowired
    private PersonalChatsRepository personalChatsRepository;

    @Override
    public PersonalChats findById(@NonNull String id) {
        return personalChatsRepository.findById(id).orElse(null);
    }

    @Override
    public PersonalChats save(@NonNull PersonalChats personalChats) {
        return personalChatsRepository.save(personalChats);
    }

    @Override
    public PersonalChats findBySenderIdAndReceiverId(@NonNull UUID senderId, @NonNull UUID receiverId) {
        return personalChatsRepository.findBySenderIdAndReceiverId(senderId.toString(), receiverId.toString()).orElse(null);
    }

    @Override
    public List<PersonalChats> findChatsBySenderIdOrReceiverId(@NonNull UUID senderId, @NonNull UUID receiverId) {
        return personalChatsRepository.findBySenderIdOrReceiverId(senderId.toString(), receiverId.toString()).orElse(new ArrayList<>());
    }
}
