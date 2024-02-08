package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.repository.ConversationRepository;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final MessagesInTransitRepository messagesInTransitRepository;

    private final ConversationRepository conversationRepository;

    private final OnlineOfflineService onlineOfflineService;

    private final UserService userService;

    @Autowired
    public ChatService(SimpMessageSendingOperations simpMessageSendingOperations, MessagesInTransitRepository messagesInTransitRepository, ConversationRepository conversationRepository, OnlineOfflineService onlineOfflineService, UserService userService){
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.messagesInTransitRepository = messagesInTransitRepository;
        this.conversationRepository = conversationRepository;
        this.onlineOfflineService = onlineOfflineService;
        this.userService = userService;
    }
    public void sendMessageToConvId(ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("{} for conv id {} for session id: {}", chatMessage.toString(), conversationId, sessionId);
        String targetUsername = chatMessage.getReceiverUsername();
        boolean isTargetOnline = onlineOfflineService.isOnline(targetUsername);
        UserDetailsImpl userDetails = userService.getUser();
        UUID fromUserId = userDetails.getId();
        UUID toUserId = chatMessage.getReceiverId();

        if(!isTargetOnline){
            MessagesInTransitEntity messagesInTransitEntity = MessagesInTransitEntity
                    .builder()
                    .id(UUID.randomUUID())
                    .read(false)
                    .time(Timestamp.from(Instant.now()))
                    .fromUser(fromUserId)
                    .toUser(toUserId)
                    .senderNotified(false)
                    .content(chatMessage.getContent())
                    .convId(conversationId)
                    .build();
            messagesInTransitRepository.save(messagesInTransitEntity);
            log.info("{} is not online. Content saved in unseen messages", chatMessage.getReceiverUsername());
        }
        ConversationEntity conversationEntity = ConversationEntity
                .builder()
                .content(chatMessage.getContent())
                .fromUser(fromUserId)
                .toUser(toUserId)
                .id(UUID.randomUUID())
                .build();
        conversationRepository.save(conversationEntity);
        simpMessageSendingOperations.convertAndSend("/topic/" + conversationId, chatMessage);
    }
}
