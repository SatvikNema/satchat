package com.satvik.satchat.controller;

import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class ConversationController {

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService){
        this.conversationService = conversationService;
    }

    @GetMapping("/{user}/friends")
    public List<UserConnection> getUserFriends(@PathVariable("user") String username){

        return conversationService.getUserFriends(username);
    }
}
