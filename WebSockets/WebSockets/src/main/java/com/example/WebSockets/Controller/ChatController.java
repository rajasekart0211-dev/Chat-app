package com.example.WebSockets.Controller;

import com.example.WebSockets.Model.Conversation;
import com.example.WebSockets.Model.Message;
import com.example.WebSockets.Model.User;
import com.example.WebSockets.Service.ChatService;
import com.example.WebSockets.dtos.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public MessageResponse sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content
    ) {
        return chatService.sendPersonalMessage(
                senderId,
                receiverId,
                content
        );
    }

    @GetMapping("/personal")
    public ResponseEntity<Conversation> getPersonalConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id
    ) {
        return chatService.findPersonalConversation(user1Id, user2Id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> getMessages(
            @PathVariable Long conversationId
    ) {
        return chatService.getMessages(conversationId);
    }

    @GetMapping("/me")
    public String getCurrentUser(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return "ID: " + user.getId()
                + ", Name: " + user.getName()
                + ", Phone: " + user.getPhoneNumber();
    }

}