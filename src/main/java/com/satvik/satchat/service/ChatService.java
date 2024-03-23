package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageDeliveryStatusEnum;
import com.satvik.satchat.repository.ConversationRepository;
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

  private final ConversationRepository conversationRepository;

  private final OnlineOfflineService onlineOfflineService;

  @Autowired
  public ChatService(
      SimpMessageSendingOperations simpMessageSendingOperations,
      ConversationRepository conversationRepository,
      OnlineOfflineService onlineOfflineService) {
    this.simpMessageSendingOperations = simpMessageSendingOperations;
    this.conversationRepository = conversationRepository;
    this.onlineOfflineService = onlineOfflineService;
  }

  public void sendMessageToConvId(
      ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor) {
    UserDetailsImpl userDetails = getUser();
    UUID fromUserId = userDetails.getId();
    UUID toUserId = chatMessage.getReceiverId();
    populateContext(chatMessage, userDetails);
    boolean isTargetOnline = onlineOfflineService.isUserOnline(toUserId);
    boolean isTargetSubscribed =
        onlineOfflineService.isUserSubscribed(toUserId, "/topic/" + conversationId);
    chatMessage.setId(UUID.randomUUID());

    ConversationEntity.ConversationEntityBuilder conversationEntityBuilder =
        ConversationEntity.builder();

    conversationEntityBuilder
        .id(chatMessage.getId())
        .fromUser(fromUserId)
        .toUser(toUserId)
        .content(chatMessage.getContent())
        .convId(conversationId);
    if (!isTargetOnline) {
      log.info(
          "{} is not online. Content saved in unseen messages", chatMessage.getReceiverUsername());
      conversationEntityBuilder.deliveryStatus(MessageDeliveryStatusEnum.NOT_DELIVERED.toString());
      chatMessage.setMessageDeliveryStatusEnum(MessageDeliveryStatusEnum.NOT_DELIVERED);

    } else if (!isTargetSubscribed) {
      log.info(
          "{} is online but not subscribed. sending to their private subscription",
          chatMessage.getReceiverUsername());
      conversationEntityBuilder.deliveryStatus(MessageDeliveryStatusEnum.DELIVERED.toString());
      chatMessage.setMessageDeliveryStatusEnum(MessageDeliveryStatusEnum.DELIVERED);
      simpMessageSendingOperations.convertAndSend("/topic/" + toUserId.toString(), chatMessage);

    } else {
      conversationEntityBuilder.deliveryStatus(MessageDeliveryStatusEnum.SEEN.toString());
      chatMessage.setMessageDeliveryStatusEnum(MessageDeliveryStatusEnum.SEEN);
    }
    conversationRepository.save(conversationEntityBuilder.build());
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
