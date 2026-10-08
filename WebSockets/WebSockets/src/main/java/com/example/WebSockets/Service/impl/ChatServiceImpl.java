package com.example.WebSockets.Service.impl;

import com.example.WebSockets.Enums.ConversationType;
import com.example.WebSockets.Enums.Role;
import com.example.WebSockets.Model.Conversation;
import com.example.WebSockets.Model.ConversationMember;
import com.example.WebSockets.Model.Message;
import com.example.WebSockets.Model.User;
import com.example.WebSockets.Repositories.ConversationMemberRepository;
import com.example.WebSockets.Repositories.ConversationRepository;
import com.example.WebSockets.Repositories.MessageRepository;
import com.example.WebSockets.Repositories.UserRepository;
import com.example.WebSockets.Service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.WebSockets.dtos.MessageResponse;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;


    @Override
    public Optional<Conversation> findPersonalConversation(Long user1Id, Long user2Id) {
        return conversationMemberRepository
                .findPersonalConversation(user1Id, user2Id);
    }

    @Override
    @Transactional
    public MessageResponse sendPersonalMessage(Long senderId, Long receiverId, String content) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Optional<Conversation> existingConversation =
                conversationMemberRepository
                        .findPersonalConversation(senderId, receiverId);

        Conversation conversation;

        if (existingConversation.isPresent()) {

            conversation = existingConversation.get();

        } else {

            Conversation newConversation = Conversation.builder()
                    .type(ConversationType.PERSONAL)
                    .build();

            conversation = conversationRepository.save(newConversation);

            ConversationMember senderMember = ConversationMember.builder()
                    .conversation(conversation)
                    .user(sender)
                    .role(Role.MEMBER)
                    .build();

            ConversationMember receiverMember = ConversationMember.builder()
                    .conversation(conversation)
                    .user(receiver)
                    .role(Role.MEMBER)
                    .build();

            conversationMemberRepository.save(senderMember);
            conversationMemberRepository.save(receiverMember);
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResponse.builder()
                .id(savedMessage.getId())
                .conversationId(savedMessage.getConversation().getId())
                .senderId(savedMessage.getSender().getId())
                .senderName(savedMessage.getSender().getName())
                .content(savedMessage.getContent())
                .sentAt(savedMessage.getSentAt())
                .build();
    }

    @Override
    public List<MessageResponse> getMessages(Long conversationId) {

        List<Message> messages =
                messageRepository.findByConversationIdOrderBySentAtAsc(
                        conversationId
                );

        return messages.stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .conversationId(message.getConversation().getId())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getName())
                        .content(message.getContent())
                        .sentAt(message.getSentAt())
                        .build())
                .toList();
    }

}
