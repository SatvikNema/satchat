package com.satvik.satchat.config.websocket;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.repository.UserRepository;
import com.satvik.satchat.service.ChatService;
import com.satvik.satchat.service.OnlineOfflineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebSocketEventListener {

    private final OnlineOfflineService onlineOfflineService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final ChatService chatService;

    private UserRepository userRepository;

    public WebSocketEventListener(OnlineOfflineService onlineOfflineService, SimpMessageSendingOperations simpMessageSendingOperations, ChatService chatService, UserRepository userRepository){
        this.onlineOfflineService = onlineOfflineService;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.chatService = chatService;
        this.userRepository = userRepository;
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        onlineOfflineService.removeOnlineUser(event.getUser());
    }

    @EventListener
    @SendToUser
    public void handleSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent){
        String subscribedChannel = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpDestination");
        if(subscribedChannel == null){
            log.error("SUBSCRIBED TO NULL?? WAT?!");
            return;
        }
        String sessionId = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpSessionId");
        log.info("subscription called for {} sessionId: {}", subscribedChannel, sessionId);
//        List<MessagesInTransitEntity> unseenMessages = chatService.getUnseenMessages(sessionSubscribeEvent.getUser(), subscribedChannel);
//        if(!CollectionUtils.isEmpty(unseenMessages)){
//            UserDetailsImpl userDetails = getUserDetails(sessionSubscribeEvent.getUser());
//            log.info("there are some unseen messages for {}", userDetails.getUsername());
//            List<UUID> fromUsersIds = unseenMessages
//                    .stream()
//                    .map(MessagesInTransitEntity::getFromUser)
//                    .toList();
//            Map<UUID, String> fromUserIdsToUsername = userRepository
//                    .findAllById(fromUsersIds)
//                    .stream()
//                    .collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername));
//
//            List<ChatMessage> chatMessages = unseenMessages
//                    .stream()
//                    .map(e -> ChatMessage
//                            .builder()
//                            .messageType(MessageType.UNSEEN)
//                            .content(e.getContent())
//                            .receiverId(e.getToUser())
//                            .receiverUsername(userDetails.getUsername())
//                            .senderId(e.getFromUser())
//                            .senderUsername(fromUserIdsToUsername.get(e.getFromUser()))
//                            .build())
//                    .toList();
//            chatMessages.forEach(message -> simpMessageSendingOperations.convertAndSend(subscribedChannel, message));
//            chatService.markSeen(unseenMessages);
//        }

    }

    private UserDetailsImpl getUserDetails(Principal principal){
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
        Object object = user.getPrincipal();
        return (UserDetailsImpl) object;
    }

    @EventListener
    public void handleConnectedEvent(SessionConnectedEvent sessionConnectedEvent){
        onlineOfflineService.addOnlineUser(sessionConnectedEvent.getUser());
    }
}
