package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.repository.ConversationRepository;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

  private final SimpMessageSendingOperations simpMessageSendingOperations;

  private final MessagesInTransitRepository messagesInTransitRepository;

  private final ConversationRepository conversationRepository;

  private final OnlineOfflineService onlineOfflineService;

  @Autowired
  public ChatService(
      SimpMessageSendingOperations simpMessageSendingOperations,
      MessagesInTransitRepository messagesInTransitRepository,
      ConversationRepository conversationRepository,
      OnlineOfflineService onlineOfflineService) {
    this.simpMessageSendingOperations = simpMessageSendingOperations;
    this.messagesInTransitRepository = messagesInTransitRepository;
    this.conversationRepository = conversationRepository;
    this.onlineOfflineService = onlineOfflineService;
  }

  public void sendMessageToConvId(
      ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    log.info(
        "{} for conv id {} for session id: {}", chatMessage.toString(), conversationId, sessionId);
    String targetUsername = chatMessage.getReceiverUsername();

    UserDetailsImpl userDetails = getUser();
    UUID fromUserId = userDetails.getId();
    UUID toUserId = chatMessage.getReceiverId();
    populateContext(chatMessage, userDetails);
    boolean isTargetOnline = onlineOfflineService.isOnline(targetUsername);
    boolean isTargetSubscribed =
        onlineOfflineService.isUserSubscribed(targetUsername, "/topic/" + conversationId);
    chatMessage.setId(UUID.randomUUID());

    if (!isTargetOnline || !isTargetSubscribed) {
      MessagesInTransitEntity messagesInTransitEntity =
          MessagesInTransitEntity.builder()
              .id(chatMessage.getId())
              .read(false)
              .time(Timestamp.from(Instant.now()))
              .fromUser(fromUserId)
              .toUser(toUserId)
              .senderNotified(false)
              .content(chatMessage.getContent())
              .convId(conversationId)
              .build();
      messagesInTransitRepository.save(messagesInTransitEntity);
      if (!isTargetOnline) {
        log.info(
            "{} is not online. Content saved in unseen messages",
            chatMessage.getReceiverUsername());
      } else {
        log.info(
            "{} is online but not subscribed. sending to their private subscription",
            chatMessage.getReceiverUsername());
        simpMessageSendingOperations.convertAndSend("/topic/" + toUserId.toString(), chatMessage);
      }
    } else {
      ConversationEntity conversationEntity =
          ConversationEntity.builder()
              .content(chatMessage.getContent())
              .fromUser(fromUserId)
              .toUser(toUserId)
              .id(chatMessage.getId())
              .build();
      conversationRepository.save(conversationEntity);
    }
    simpMessageSendingOperations.convertAndSend("/topic/" + conversationId, chatMessage);
  }

  private void populateContext(ChatMessage chatMessage, UserDetailsImpl userDetails) {
    chatMessage.setSenderUsername(userDetails.getUsername());
    chatMessage.setSenderId(userDetails.getId());
  }

  public UserDetailsImpl getUser() {
    Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return (UserDetailsImpl) object;
  }
}
