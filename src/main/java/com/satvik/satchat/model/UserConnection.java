package com.satvik.satchat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserConnection {
    private UUID connectionId;
    private String connectionUsername;
    private String convId;
}
