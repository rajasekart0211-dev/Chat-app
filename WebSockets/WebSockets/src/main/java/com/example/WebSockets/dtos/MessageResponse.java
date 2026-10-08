package com.example.WebSockets.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MessageResponse {

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String senderName;

    private String content;

    private LocalDateTime sentAt;
}