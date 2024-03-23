package com.satvik.satchat.mapper;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

  private final UserRepository userRepository;

  public ChatMessageMapper(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<ChatMessage> toChatMessages(
      List<ConversationEntity> conversationEntities,
      UserDetailsImpl userDetails,
      MessageType messageType) {
    List<UUID> fromUsersIds =
        conversationEntities.stream().map(ConversationEntity::getFromUser).toList();
    Map<UUID, String> fromUserIdsToUsername =
        userRepository.findAllById(fromUsersIds).stream()
            .collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername));

    return conversationEntities.stream()
        .map(e -> toChatMessage(e, userDetails, fromUserIdsToUsername, messageType))
        .toList();
  }

  private static ChatMessage toChatMessage(
      ConversationEntity e,
      UserDetailsImpl userDetails,
      Map<UUID, String> fromUserIdsToUsername,
      MessageType messageType) {
    return ChatMessage.builder()
        .id(e.getId())
        .messageType(messageType)
        .content(e.getContent())
        .receiverId(e.getToUser())
        .receiverUsername(userDetails.getUsername())
        .senderId(e.getFromUser())
        .senderUsername(fromUserIdsToUsername.get(e.getFromUser()))
        .build();
  }
}
