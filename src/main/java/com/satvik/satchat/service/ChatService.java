package com.satvik.satchat.service;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.repository.ConversationRepository;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final MessagesInTransitRepository messagesInTransitRepository;

    private final ConversationRepository conversationRepository;

    @Autowired
    public ChatService(SimpMessageSendingOperations simpMessageSendingOperations, MessagesInTransitRepository messagesInTransitRepository, ConversationRepository conversationRepository){
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.messagesInTransitRepository = messagesInTransitRepository;
        this.conversationRepository = conversationRepository;
    }
    public void sendMessageToConvId(ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        log.info("{} for conv id {} for session id: {}", chatMessage.toString(), conversationId, sessionId);


        simpMessageSendingOperations.convertAndSend("/topic/"+conversationId, chatMessage);
    }
}
