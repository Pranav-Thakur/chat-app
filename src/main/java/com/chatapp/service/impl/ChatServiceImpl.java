package com.chatapp.service.impl;

import com.chatapp.ChatAppUtil;
import com.chatapp.dao.model.AppUserDO;
import com.chatapp.dao.model.ChatMessage;
import com.chatapp.dao.model.PersonalChats;
import com.chatapp.dao.service.AppUserRepoService;
import com.chatapp.dao.service.ChatMessageRepoService;
import com.chatapp.dao.service.PersonalChatsRepoService;
import com.chatapp.model.Message;
import com.chatapp.payload.response.UserHistoryChatsResponse;
import com.chatapp.payload.response.UserListChatsResponse;
import com.chatapp.service.ChatService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AppUserRepoService appUserRepoService;

    @Autowired
    private PersonalChatsRepoService personalChatsRepoService;

    @Autowired
    private ChatMessageRepoService chatMessageRepoService;

    @Value("${chatapp.chatid.suffix.length}")
    private int chatIdSuffixLength;

    @Value("${chatapp.chats.default.size}")
    private int chatDefaultSize;

    @Override
    public Message saveMessage(@NonNull Message message) {
        UUID senderId = message.getSenderId();
        UUID receiverId = message.getReceiverId();

        // Ensure consistent ordering of IDs
        if (senderId.toString().compareTo(receiverId.toString()) > 0) {
            UUID temp = senderId;
            senderId = receiverId;
            receiverId = temp;
        }

        final String _id = "personal_" + senderId + "_" + receiverId;
        PersonalChats chatThread = personalChatsRepoService.findById(_id);

        if (chatThread == null) {
            chatThread = new PersonalChats(_id, senderId.toString(), receiverId.toString(), new ArrayList<>());
        }

        String chatId = "chat_" + message.getSenderId() + "_" + message.getTimestamp() + "_" + ChatAppUtil.randomOtp(chatIdSuffixLength);
        ChatMessage newMsg = new ChatMessage(chatId, message.getSenderId().toString(), message.getReceiverId().toString(),
                message.getContent(), message.getTimestamp(), message.getDeliveryTimestamp(), message.getReadTimestamp(), message.isRead());
        newMsg = chatMessageRepoService.save(newMsg);
        message.setMessageId(newMsg.getId());

        chatThread.getMessages().add(newMsg.getId());
        if (chatThread.getMessages().size() > chatDefaultSize) {
            int toRemove = chatThread.getMessages().size() - chatDefaultSize;
            chatThread.getMessages().subList(0, toRemove).clear();
        }

        chatThread = personalChatsRepoService.save(chatThread);
        return message;
    }

    @Override
    public List<UserListChatsResponse> fetchListChats(@NonNull UUID userId) {
        List<PersonalChats> threads = personalChatsRepoService.findChatsBySenderIdOrReceiverId(userId, userId);

        List<UserListChatsResponse> result = new ArrayList<>();
        for (PersonalChats thread : threads) {
            String otherUserIdStr = thread.getSenderId().equals(userId.toString()) ? thread.getReceiverId() : thread.getSenderId();
            UUID otherUserId = UUID.fromString(otherUserIdStr);
            List<String> messages = thread.getMessages();
            if (messages.isEmpty()) continue;

            List<ChatMessage> chatMessages = chatMessageRepoService.getByIds(messages);
            ChatMessage last = chatMessages.get(messages.size() - 1);
            long unread = chatMessages.stream()
                    .filter(m -> m.getSenderId().equals(otherUserIdStr) && !m.isRead())
                    .count();

            AppUserDO otherUser = appUserRepoService.getAppUserById(otherUserId); // fetch name/phone

            result.add(new UserListChatsResponse(
                    otherUser.getUserId(),
                    otherUser.getName(),
                    otherUser.getPhone(),
                    last.getContent(),
                    last.getTimestamp(),
                    unread
            ));
        }

        return result;
    }

    @Override
    public List<UserHistoryChatsResponse> fetchListChats(@NonNull UUID userId, @NonNull UUID otherUserId) {
        UUID senderId = userId;
        UUID receiverId = otherUserId;
        // Ensure consistent ordering of IDs
        if (senderId.toString().compareTo(receiverId.toString()) > 0) {
            UUID temp = senderId;
            senderId = receiverId;
            receiverId = temp;
        }

        List<UserHistoryChatsResponse> response = new ArrayList<>();
        PersonalChats personalChat = personalChatsRepoService.findBySenderIdAndReceiverId(senderId, receiverId);
        if (personalChat == null) {
            return response;
        }

        AppUserDO currentUser = appUserRepoService.getAppUserById(userId); // fetch name/phone
        AppUserDO otherUser = appUserRepoService.getAppUserById(otherUserId);

        List<String> messages = personalChat.getMessages();
        if (messages.isEmpty()) return response;

        List<ChatMessage> chatMessages = chatMessageRepoService.getByIds(messages);
        boolean readAlready = false;
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage chatMessage =  chatMessages.get(i);
            if (!readAlready && chatMessage.isRead()) {
                readAlready = true;
            }

            if (readAlready && !chatMessage.isRead()) {
                chatMessage.setRead(readAlready);
                if (chatMessage.getDeliveredAt() == 0) chatMessage.setDeliveredAt(chatMessage.getTimestamp());
                if (chatMessage.getReadAt() == 0) chatMessage.setReadAt(chatMessage.getTimestamp());
                chatMessage = chatMessageRepoService.save(chatMessage);
            }

            UserHistoryChatsResponse chatsResponse = getUserHistoryChatsResponse(chatMessage, currentUser, otherUser);
            response.add(chatsResponse);
        }

        response.sort(Comparator.comparingLong(UserHistoryChatsResponse::getTimestamp));
        return response;
    }

    @Override
    public Message markMessageAsDelivered(@NonNull Message message) {
        ChatMessage chatMessage = chatMessageRepoService.getById(message.getMessageId());
        if (chatMessage != null) {
            chatMessage.setRead(message.isRead());
            chatMessage.setDeliveredAt(message.getDeliveryTimestamp());
            chatMessage.setReadAt(message.getReadTimestamp());
            chatMessage = chatMessageRepoService.save(chatMessage);
        }
        return message;
    }

    private UserHistoryChatsResponse getUserHistoryChatsResponse(@NonNull ChatMessage chatMessage,
                                                                 @NonNull AppUserDO currentUser, @NonNull AppUserDO otherUser) {
        UserHistoryChatsResponse chatsResponse = new UserHistoryChatsResponse();

        if (chatMessage.getSenderId().equals(currentUser.getUserId().toString())) {
            chatsResponse.setSenderId(currentUser.getUserId());
            chatsResponse.setName(currentUser.getName());
            chatsResponse.setPhone(currentUser.getPhone());
        } else {
            chatsResponse.setSenderId(otherUser.getUserId());
            chatsResponse.setName(otherUser.getName());
            chatsResponse.setPhone(otherUser.getPhone());
        }

        chatsResponse.setMessage(chatMessage.getContent());
        chatsResponse.setTimestamp(chatMessage.getTimestamp());
        chatsResponse.setDeliveredTimestamp(chatMessage.getDeliveredAt());
        chatsResponse.setReadTimestamp(chatMessage.getReadAt());
        chatsResponse.setRead(chatMessage.isRead());
        return chatsResponse;
    }
}
