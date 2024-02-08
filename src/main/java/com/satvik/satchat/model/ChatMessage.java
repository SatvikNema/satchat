package com.satvik.satchat.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessage {
    private String content;
    private MessageType messageType;

    private UUID senderId;
    private String senderUsername;

    private UUID receiverId;
    private String receiverUsername;
}
