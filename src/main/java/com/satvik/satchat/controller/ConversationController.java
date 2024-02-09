package com.satvik.satchat.controller;

import com.satvik.satchat.entity.MessagesInTransitEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(("/api/conversation"))
public class ConversationController {

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService){
        this.conversationService = conversationService;
    }

    @GetMapping("/friends")
    public List<UserConnection> getUserFriends(){
        return conversationService.getUserFriends();
    }

    @GetMapping("/unseenMessages")
    public List<ChatMessage> getUnseenMessages(){
        return conversationService.getUnseenMessages();
    }

    @PutMapping("/setReadMessages")
    public List<ChatMessage> setReadMessages(@RequestBody List<ChatMessage> chatMessages){
        return conversationService.setReadMessages(chatMessages);
    }
}
