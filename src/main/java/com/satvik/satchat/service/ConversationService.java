package com.satvik.satchat.service;

import static com.satvik.satchat.DbBoiii.getConvId;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.exception.EntityException;
import com.satvik.satchat.mapper.ChatMessageMapper;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.model.UnseenMessageCountResponse;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.repository.ConversationRepository;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import com.satvik.satchat.repository.UserRepository;
import com.satvik.satchat.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class ConversationService {
  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;

  private final ChatMessageMapper chatMessageMapper;

  private final MessagesInTransitRepository messagesInTransitRepository;

  private final ConversationRepository conversationRepository;

  public ConversationService(
      UserRepository userRepository,
      SecurityUtils securityUtils,
      MessagesInTransitRepository messagesInTransitRepository,
      ChatMessageMapper chatMessageMapper,
      ConversationRepository conversationRepository) {
    this.userRepository = userRepository;
    this.securityUtils = securityUtils;
    this.messagesInTransitRepository = messagesInTransitRepository;
    this.chatMessageMapper = chatMessageMapper;
    this.conversationRepository = conversationRepository;
  }

  public List<UserConnection> getUserFriends() {
    UserDetailsImpl userDetails = securityUtils.getUser();
    String username = userDetails.getUsername();
    List<UserEntity> users = userRepository.findAll();
    UserEntity thisUser =
        users.stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst()
            .orElseThrow(EntityException::new);

    return users.stream()
        .filter(user -> !user.getUsername().equals(username))
        .map(
            user ->
                UserConnection.builder()
                    .connectionId(user.getId())
                    .connectionUsername(user.getUsername())
                    .convId(getConvId(user, thisUser))
                    .build())
        .toList();
  }

  public List<UnseenMessageCountResponse> getUnseenMessages() {
    List<UnseenMessageCountResponse> result = new ArrayList<>();
    UserDetailsImpl userDetails = securityUtils.getUser();
    List<Object[]> unseenMessages =
        messagesInTransitRepository.findUnseenMessagesCount(userDetails.getId());
    if (!CollectionUtils.isEmpty(unseenMessages)) {
      log.info("there are some unseen messages for {}", userDetails.getUsername());
      //            result = chatMessageMapper.toChatMessages(unseenMessages, userDetails,
      // MessageType.UNSEEN);
      result =
          unseenMessages.stream()
              .map(
                  unseenMessage ->
                      UnseenMessageCountResponse.builder()
                          .count((Long) unseenMessage[1])
                          .fromUser((UUID) unseenMessage[0])
                          .build())
              .toList();
    }
    return result;
  }

  public List<ChatMessage> getUnseenMessages(UUID fromUserId) {
    List<ChatMessage> result = new ArrayList<>();
    UserDetailsImpl userDetails = securityUtils.getUser();
    List<MessagesInTransitEntity> unseenMessages =
        messagesInTransitRepository.findUnseenMessages(userDetails.getId(), fromUserId);
    if (!CollectionUtils.isEmpty(unseenMessages)) {
      log.info(
          "there are some unseen messages for {} from {}", userDetails.getUsername(), fromUserId);
      result = chatMessageMapper.toChatMessages(unseenMessages, userDetails, MessageType.UNSEEN);
    }
    return result;
  }

  public List<ChatMessage> setReadMessages(List<ChatMessage> chatMessages) {
    List<UUID> inTransitMessageIds = chatMessages.stream().map(ChatMessage::getId).toList();
    List<MessagesInTransitEntity> messagesInTransitEntities =
        messagesInTransitRepository.findAllById(inTransitMessageIds);
    messagesInTransitEntities.forEach(message -> message.setRead(true));
    List<MessagesInTransitEntity> saved =
        messagesInTransitRepository.saveAll(messagesInTransitEntities);

    List<ConversationEntity> conversationEntities =
        chatMessages.stream()
            .map(
                chatMessage ->
                    ConversationEntity.builder()
                        .content(chatMessage.getContent())
                        .fromUser(chatMessage.getSenderId())
                        .toUser(chatMessage.getReceiverId())
                        .id(UUID.randomUUID())
                        .build())
            .toList();
    conversationRepository.saveAll(conversationEntities);

    return chatMessageMapper.toChatMessages(saved, securityUtils.getUser(), MessageType.CHAT);
  }
}
