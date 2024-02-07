package com.satvik.satchat.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType messageType;
}
