package com.chatapp.controller;

import com.chatapp.payload.request.UserRegisterRequest;
import com.chatapp.payload.response.UserHistoryChatsResponse;
import com.chatapp.payload.response.UserListChatsResponse;
import com.chatapp.payload.response.UserRegisterResponse;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest registerRequest) throws Exception {
        UserRegisterResponse resp = userService.register(registerRequest);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/check-session")
    public ResponseEntity<UserRegisterResponse> checkSession(@PathVariable UUID id) throws Exception {
        UserRegisterResponse resp = userService.checkUser(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/find")
    public ResponseEntity<UserRegisterResponse> findUser(@PathVariable UUID id, @RequestParam(name = "peerId", required = false) UUID otherUserId,
                                                         @RequestParam(name = "phone", required = false) String otherUserPhone) throws Exception {
        return ResponseEntity.ok(userService.findUser(id, otherUserId, otherUserPhone));
    }

    @GetMapping("/{id}/chats")
    public ResponseEntity<List<UserListChatsResponse>> listChats(@PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(userService.listChats(id));
    }

    @GetMapping("/{id}/chat-history/{otherId}")
    public ResponseEntity<List<UserHistoryChatsResponse>> historyChats(@PathVariable UUID id, @PathVariable UUID otherId) throws Exception {
        return ResponseEntity.ok(userService.historyChats(id, otherId));
    }
}
