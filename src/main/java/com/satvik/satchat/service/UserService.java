package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserEntity findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    public UserDetailsImpl getUser(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (UserDetailsImpl) object;
    }
}
