package com.satvik.satchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupRequest {
    private String username;

    private String email;

    private Set<String> role;

    private String password;
}
