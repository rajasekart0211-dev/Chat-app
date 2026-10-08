package com.example.WebSockets.Service;

import com.example.WebSockets.Model.Conversation;
import com.example.WebSockets.dtos.MessageResponse;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    Optional<Conversation> findPersonalConversation(
            Long user1Id,
            Long user2Id
    );

    MessageResponse sendPersonalMessage(
            Long senderId,
            Long receiverId,
            String content
    );

    List<MessageResponse> getMessages(Long conversationId);
}
