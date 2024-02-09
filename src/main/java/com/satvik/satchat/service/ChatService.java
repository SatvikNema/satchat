package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.repository.ConversationRepository;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import com.satvik.satchat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final MessagesInTransitRepository messagesInTransitRepository;

    private final ConversationRepository conversationRepository;

    private final OnlineOfflineService onlineOfflineService;

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(SimpMessageSendingOperations simpMessageSendingOperations, MessagesInTransitRepository messagesInTransitRepository, ConversationRepository conversationRepository, OnlineOfflineService onlineOfflineService, UserService userService, UserRepository userRepository){
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.messagesInTransitRepository = messagesInTransitRepository;
        this.conversationRepository = conversationRepository;
        this.onlineOfflineService = onlineOfflineService;
        this.userService = userService;
        this.userRepository = userRepository;
    }
    public void sendMessageToConvId(ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("{} for conv id {} for session id: {}", chatMessage.toString(), conversationId, sessionId);
        String targetUsername = chatMessage.getReceiverUsername();
        boolean isTargetOnline = onlineOfflineService.isOnline(targetUsername);
        UserDetailsImpl userDetails = getUser();
        UUID fromUserId = userDetails.getId();
        UUID toUserId = chatMessage.getReceiverId();
        populateContext(chatMessage, userDetails);

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

    private void populateContext(ChatMessage chatMessage, UserDetailsImpl userDetails) {
        chatMessage.setSenderUsername(userDetails.getUsername());
        chatMessage.setSenderId(userDetails.getId());
    }

    public List<MessagesInTransitEntity> getUnseenMessages(Principal user, String subscribedChannel) {
        UserDetailsImpl userDetails = getUserDetails(user);
        List<MessagesInTransitEntity> unseenMessages = messagesInTransitRepository.findUnseenMessages(userDetails.getId());

        return unseenMessages;
    }

    private UserDetailsImpl getUserDetails(Principal principal){
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
        Object object = user.getPrincipal();
        return (UserDetailsImpl) object;
    }

    public UserDetailsImpl getUser(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (UserDetailsImpl) object;
    }

    public void markSeen(List<MessagesInTransitEntity> unseenMessages) {
        // todo impl
    }
}
