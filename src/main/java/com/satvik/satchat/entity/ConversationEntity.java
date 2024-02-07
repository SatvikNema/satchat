package com.satvik.satchat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversation", schema = "public", catalog = "postgres")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConversationEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "from_user", columnDefinition = "uuid")
    private UUID fromUser;

    @Column(name = "to_user", columnDefinition = "uuid")
    private UUID toUser;

    @Column(name = "time")
    private Timestamp time;

    @Column(name = "content", length = -1)
    private String content;
}
