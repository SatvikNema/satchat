package com.satvik.satchat.config;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    public WebSocketEventListener(SimpMessageSendingOperations simpMessageSendingOperations){
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){

        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) stompHeaderAccessor.getSessionAttributes().get("username");
        String sessionId = stompHeaderAccessor.getSessionId();
        if(username != null){
            log.info("user {} with sessionId {} is disconnected", username, sessionId);
            var chatMessage = ChatMessage
                    .builder()
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .build();
            simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }

    @EventListener
    @SendToUser
    public void handleSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent){
        String destination = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpDestination");
        String sessionId = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpSessionId");
        log.info("subscription called for {} sessionId: {}", destination, sessionId);

    }

    @EventListener
    public void handleConnectedEvent(SessionConnectedEvent sessionConnectedEvent){
        String user = sessionConnectedEvent.getUser() == null ? null : sessionConnectedEvent.getUser().getName();
        System.out.println(user+" connected");
    }

    @EventListener
    public void handleConnectedEvent(SessionDisconnectEvent sessionDisconnectEvent){
        String user = sessionDisconnectEvent.getUser() == null ? null : sessionDisconnectEvent.getUser().getName();
        System.out.println(user +" disconnected");
    }
}
