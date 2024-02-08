package com.satvik.satchat.config.websocket;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageType;
import com.satvik.satchat.service.OnlineOfflineService;
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
public class WebSocketEventListener {

    private final OnlineOfflineService onlineOfflineService;

    public WebSocketEventListener(OnlineOfflineService onlineOfflineService){
        this.onlineOfflineService = onlineOfflineService;
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        onlineOfflineService.removeOnlineUser(event.getUser());
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
        onlineOfflineService.addOnlineUser(sessionConnectedEvent.getUser());
    }
}
