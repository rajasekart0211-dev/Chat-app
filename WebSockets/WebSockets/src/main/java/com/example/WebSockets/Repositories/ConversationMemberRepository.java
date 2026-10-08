package com.example.WebSockets.Repositories;

import com.example.WebSockets.Model.Conversation;
import com.example.WebSockets.Model.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

    @Query("""
    SELECT cm.conversation
    FROM ConversationMember cm
    WHERE cm.conversation.type =
        com.example.WebSockets.Enums.ConversationType.PERSONAL
    AND cm.user.id IN (:userId1, :userId2)
    GROUP BY cm.conversation
    HAVING COUNT(DISTINCT cm.user.id) = 2
       AND COUNT(cm) = 2
""")
    Optional<Conversation> findPersonalConversation(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );
}
