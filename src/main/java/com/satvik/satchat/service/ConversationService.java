package com.satvik.satchat.service;

import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.exception.EntityException;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.satvik.satchat.DbBoiii.getConvId;

@Service
public class ConversationService {
    private UserRepository userRepository;
    public ConversationService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public List<UserConnection> getUserFriends(String username) {
        List<UserEntity> users = userRepository.findAll();
        UserEntity thisUser = users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(EntityException::new);

        return users.stream()
                .filter(user -> !user.getUsername().equals(username))
                .map(user ->
                        UserConnection
                                .builder()
                                .connectionId(user.getId())
                                .connectionUsername(user.getUsername())
                                .convId(getConvId(user, thisUser))
                                .build())
                .toList();
    }
}
