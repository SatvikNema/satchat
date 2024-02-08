package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.model.UserResponse;
import com.satvik.satchat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
public class OnlineOfflineService {
    private final Set<String> onlineUsers;

    private final UserRepository userRepository;

    public OnlineOfflineService(UserRepository userRepository){
        this.onlineUsers = new ConcurrentSkipListSet<>();
        this.userRepository = userRepository;
    }
    public void addOnlineUser(Principal user) {
        if(user!=null) {
            UserDetailsImpl userDetails = getUserDetails(user);
            log.info("{} is online", userDetails.getUsername());
            onlineUsers.add(userDetails.getUsername());
            // todo broadcast to all oneline friends of 'user' that it has come online
        }
    }

    public void removeOnlineUser(Principal user) {
        if(user != null) {
            UserDetailsImpl userDetails = getUserDetails(user);
            log.info("{} went offline", userDetails.getUsername());
            onlineUsers.remove(userDetails.getUsername());

            // todo broadcast to all oneline friends of 'user' that it has went offline
        }
    }

    public boolean isOnline(String username){
        return onlineUsers.contains(username);
    }

    private UserDetailsImpl getUserDetails(Principal principal){
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
        Object object = user.getPrincipal();
        return (UserDetailsImpl) object;
    }

    public List<UserResponse> getOnlineUsers() {
        return userRepository
                .findAllByUsernameIn(onlineUsers)
                .stream()
                .map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail()))
                .toList();
    }
}
