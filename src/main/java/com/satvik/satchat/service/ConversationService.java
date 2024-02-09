package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.exception.EntityException;
import com.satvik.satchat.mapper.ChatMessageMapper;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.repository.MessagesInTransitRepository;
import com.satvik.satchat.repository.UserRepository;
import com.satvik.satchat.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.satvik.satchat.DbBoiii.getConvId;

@Service
@Slf4j
public class ConversationService {
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    private final ChatMessageMapper chatMessageMapper;

    private final MessagesInTransitRepository messagesInTransitRepository;
    public ConversationService(UserRepository userRepository, SecurityUtils securityUtils, MessagesInTransitRepository messagesInTransitRepository, ChatMessageMapper chatMessageMapper){
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.messagesInTransitRepository = messagesInTransitRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    public List<UserConnection> getUserFriends() {
        UserDetailsImpl userDetails = securityUtils.getUser();
        String username = userDetails.getUsername();
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

    public List<ChatMessage> getUnseenMessages() {
        List<ChatMessage> result = new ArrayList<>();
        UserDetailsImpl userDetails = securityUtils.getUser();
        List<MessagesInTransitEntity> unseenMessages = messagesInTransitRepository.findUnseenMessages(userDetails.getId());
        if(!CollectionUtils.isEmpty(unseenMessages)) {
            log.info("there are some unseen messages for {}", userDetails.getUsername());
            result = chatMessageMapper.toChatMessages(unseenMessages, userDetails, MessageType.UNSEEN);
        }
        return result;
    }

    public List<ChatMessage> setReadMessages(List<ChatMessage> chatMessages) {
        List<UUID> inTransitMessageIds = chatMessages
                .stream()
                .map(ChatMessage::getId)
                .toList();
        List<MessagesInTransitEntity> messagesInTransitEntities = messagesInTransitRepository.findAllById(inTransitMessageIds);
        messagesInTransitEntities.forEach(message -> message.setRead(true));
        List<MessagesInTransitEntity> saved = messagesInTransitRepository.saveAll(messagesInTransitEntities);
        return chatMessageMapper.toChatMessages(saved, securityUtils.getUser(), MessageType.CHAT);
    }
}
