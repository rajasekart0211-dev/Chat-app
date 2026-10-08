package com.example.WebSockets.Repositories;

import com.example.WebSockets.Model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
