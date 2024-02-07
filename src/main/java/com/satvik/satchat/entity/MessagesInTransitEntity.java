package com.satvik.satchat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "messages_in_transit", schema = "public", catalog = "postgres")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MessagesInTransitEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "from_user", nullable = true, columnDefinition = "uuid")
    private UUID fromUser;

    @Column(name = "to_user", nullable = true, columnDefinition = "uuid")
    private UUID toUser;

    @Column(name = "time", nullable = true)
    private Timestamp time;

    @Column(name = "read", nullable = true)
    private Boolean read;

    @Column(name = "sender_notified", nullable = true)
    private Boolean senderNotified;
}
