package com.example.WebSockets.Controller;

import com.example.WebSockets.Service.ChatService;
import com.example.WebSockets.dtos.MessageResponse;
import com.example.WebSockets.dtos.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/personal")
    public void sendPersonalMessage(SendMessageRequest request) {

        MessageResponse response =
                chatService.sendPersonalMessage(
                        request.getSenderId(),
                        request.getReceiverId(),
                        request.getContent()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + response.getConversationId(),
                response
        );

        // Temporary bootstrap destination.
        // Later this will become /user/queue/messages
        // after Spring Security authentication is added.
        messagingTemplate.convertAndSend(
                "/topic/user/" + request.getSenderId(),
                response
        );
    }
}