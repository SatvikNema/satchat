package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageDeliveryStatusEnum;
import com.satvik.satchat.model.MessageDeliveryStatusUpdate;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.model.UserResponse;
import com.satvik.satchat.repository.UserRepository;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OnlineOfflineService {
  private final Set<UUID> onlineUsers;
  private final Map<UUID, Set<String>> userSubscribed;
  private final UserRepository userRepository;
  private final SimpMessageSendingOperations simpMessageSendingOperations;

  public OnlineOfflineService(
      UserRepository userRepository, SimpMessageSendingOperations simpMessageSendingOperations) {
    this.onlineUsers = new ConcurrentSkipListSet<>();
    this.userSubscribed = new ConcurrentHashMap<>();
    this.userRepository = userRepository;
    this.simpMessageSendingOperations = simpMessageSendingOperations;
  }

  public void addOnlineUser(Principal user) {
    if (user == null) return;
    UserDetailsImpl userDetails = getUserDetails(user);
    log.info("{} is online", userDetails.getUsername());
    for (UUID id : onlineUsers) {
      simpMessageSendingOperations.convertAndSend(
          "/topic/" + id,
          ChatMessage.builder()
              .messageType(MessageType.FRIEND_ONLINE)
              .userConnection(UserConnection.builder().connectionId(userDetails.getId()).build())
              .build());
    }
    onlineUsers.add(userDetails.getId());
  }

  public void removeOnlineUser(Principal user) {
    if (user != null) {
      UserDetailsImpl userDetails = getUserDetails(user);
      log.info("{} went offline", userDetails.getUsername());
      onlineUsers.remove(userDetails.getId());
      userSubscribed.remove(userDetails.getId());
      for (UUID id : onlineUsers) {
        simpMessageSendingOperations.convertAndSend(
            "/topic/" + id,
            ChatMessage.builder()
                .messageType(MessageType.FRIEND_OFFLINE)
                .userConnection(UserConnection.builder().connectionId(userDetails.getId()).build())
                .build());
      }
    }
  }

  public boolean isUserOnline(UUID userId) {
    return onlineUsers.contains(userId);
  }

  private UserDetailsImpl getUserDetails(Principal principal) {
    UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
    Object object = user.getPrincipal();
    return (UserDetailsImpl) object;
  }

  public List<UserResponse> getOnlineUsers() {
    return userRepository.findAllById(onlineUsers).stream()
        .map(
            userEntity ->
                new UserResponse(
                    userEntity.getId(), userEntity.getUsername(), userEntity.getEmail()))
        .toList();
  }

  public void addUserSubscribed(Principal user, String subscribedChannel) {
    UserDetailsImpl userDetails = getUserDetails(user);
    log.info("{} subscribed to {}", userDetails.getUsername(), subscribedChannel);
    Set<String> subscriptions = userSubscribed.getOrDefault(userDetails.getId(), new HashSet<>());
    subscriptions.add(subscribedChannel);
    userSubscribed.put(userDetails.getId(), subscriptions);
  }

  public void removeUserSubscribed(Principal user, String subscribedChannel) {
    UserDetailsImpl userDetails = getUserDetails(user);
    log.info("unsubscription! {} unsubscribed {}", userDetails.getUsername(), subscribedChannel);
    Set<String> subscriptions = userSubscribed.getOrDefault(userDetails.getId(), new HashSet<>());
    subscriptions.remove(subscribedChannel);
    userSubscribed.put(userDetails.getId(), subscriptions);
  }

  public boolean isUserSubscribed(UUID username, String subscription) {
    Set<String> subscriptions = userSubscribed.getOrDefault(username, new HashSet<>());
    return subscriptions.contains(subscription);
  }

  public Map<String, Set<String>> getUserSubscribed() {
    Map<String, Set<String>> result = new HashMap<>();
    List<UserEntity> users = userRepository.findAllById(userSubscribed.keySet());
    users.forEach(user -> result.put(user.getUsername(), userSubscribed.get(user.getId())));
    return result;
  }

  public void notifySender(
      UUID senderId,
      List<ConversationEntity> entities,
      MessageDeliveryStatusEnum messageDeliveryStatusEnum) {
    if (!isUserOnline(senderId)) {
      log.info(
          "{} is not online. cannot inform the socket. will persist in database",
          senderId.toString());
      return;
    }
    List<MessageDeliveryStatusUpdate> messageDeliveryStatusUpdates =
        entities.stream()
            .map(
                e ->
                    MessageDeliveryStatusUpdate.builder()
                        .id(e.getId())
                        .messageDeliveryStatusEnum(messageDeliveryStatusEnum)
                        .content(e.getContent())
                        .build())
            .toList();
    for (ConversationEntity entity : entities) {
      simpMessageSendingOperations.convertAndSend(
          "/topic/" + senderId,
          ChatMessage.builder()
              .id(entity.getId())
              .messageDeliveryStatusUpdates(messageDeliveryStatusUpdates)
              .messageType(MessageType.MESSAGE_DELIVERY_UPDATE)
              .content(entity.getContent())
              .build());
    }
  }
}
