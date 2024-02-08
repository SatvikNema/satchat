package com.satvik.satchat.controller;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.model.UserResponse;
import com.satvik.satchat.service.OnlineOfflineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {
    private final OnlineOfflineService onlineOfflineService;
    public UserController(OnlineOfflineService onlineOfflineService){
        this.onlineOfflineService = onlineOfflineService;
    }

    @GetMapping("/online")
    @PreAuthorize("hasAuthority('ADMIN')")
    List<UserResponse> getOnlineUsers(){
        return onlineOfflineService.getOnlineUsers();
    }
}
