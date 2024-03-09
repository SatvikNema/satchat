package com.satvik.satchat.model;

import lombok.Data;

@Data
public class LoginRequest {
  private String username;
  private String password;
}
