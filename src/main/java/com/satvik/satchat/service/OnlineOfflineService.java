package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.model.UserResponse;
import com.satvik.satchat.repository.UserRepository;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OnlineOfflineService {
  private final Set<String> onlineUsers;

  private final Map<String, Set<String>> userSubscribed;

  private final UserRepository userRepository;

  public OnlineOfflineService(UserRepository userRepository) {
    this.onlineUsers = new ConcurrentSkipListSet<>();
    this.userSubscribed = new ConcurrentHashMap<>();
    this.userRepository = userRepository;
  }

  public void addOnlineUser(Principal user) {
    if (user != null) {
      UserDetailsImpl userDetails = getUserDetails(user);
      log.info("{} is online", userDetails.getUsername());
      onlineUsers.add(userDetails.getUsername());
      // todo broadcast to all oneline friends of 'user' that it has come online
    }
  }

  public void removeOnlineUser(Principal user) {
    if (user != null) {
      UserDetailsImpl userDetails = getUserDetails(user);
      log.info("{} went offline", userDetails.getUsername());
      onlineUsers.remove(userDetails.getUsername());
      userSubscribed.remove(userDetails.getUsername());

      // todo broadcast to all oneline friends of 'user' that it has went offline
    }
  }

  public boolean isOnline(String username) {
    return onlineUsers.contains(username);
  }

  private UserDetailsImpl getUserDetails(Principal principal) {
    UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
    Object object = user.getPrincipal();
    return (UserDetailsImpl) object;
  }

  public List<UserResponse> getOnlineUsers() {
    return userRepository.findAllByUsernameIn(onlineUsers).stream()
        .map(
            userEntity ->
                new UserResponse(
                    userEntity.getId(), userEntity.getUsername(), userEntity.getEmail()))
        .toList();
  }

  public void addUserSubscribed(Principal user, String subscribedChannel) {
    UserDetailsImpl userDetails = getUserDetails(user);
    log.info("{} subscribed to {}", userDetails.getUsername(), subscribedChannel);
    Set<String> subscriptions =
        userSubscribed.getOrDefault(userDetails.getUsername(), new HashSet<>());
    subscriptions.add(subscribedChannel);
    userSubscribed.put(userDetails.getUsername(), subscriptions);
  }

  public void removeUserSubscribed(Principal user, String subscribedChannel) {
    UserDetailsImpl userDetails = getUserDetails(user);
    log.info("unsubscription! {} unsubscribed {}", userDetails.getUsername(), subscribedChannel);
    Set<String> subscriptions =
        userSubscribed.getOrDefault(userDetails.getUsername(), new HashSet<>());
    subscriptions.remove(subscribedChannel);
    userSubscribed.put(userDetails.getUsername(), subscriptions);
  }

  public boolean isUserSubscribed(String username, String subscription) {
    Set<String> subscriptions = userSubscribed.getOrDefault(username, new HashSet<>());
    return subscriptions.contains(subscription);
  }

  public Map<String, Set<String>> getUserSubscribed() {
    return userSubscribed;
  }
}
