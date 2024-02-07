package com.satvik.satchat.controller;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class Chat {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final ChatService chatService;

    @Autowired
    public Chat(SimpMessageSendingOperations simpMessageSendingOperations, ChatService chatService){
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.chatService = chatService;
    }

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        log.info("message recieved: {}", chatMessage.toString());
        String sessionId = headerAccessor.getSessionId();
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat/sendMessage/{convId}")
    public ChatMessage sendMessageToConvId(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable("convId") String conversationId){
        String sessionId = headerAccessor.getSessionId();
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        log.info("{} for conv id {} for session id: {}", chatMessage.toString(), conversationId, sessionId);
        simpMessageSendingOperations.convertAndSend("/topic/"+conversationId, chatMessage);

//        chatService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
        return chatMessage;
    }
}
