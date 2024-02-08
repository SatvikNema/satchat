package com.satvik.satchat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "messages_in_transit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class MessagesInTransitEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "from_user", columnDefinition = "uuid")
    private UUID fromUser;

    @Column(name = "to_user", columnDefinition = "uuid")
    private UUID toUser;

    @Column(name = "time")
    @CreatedDate
    private Timestamp time;

    @Column(name = "read")
    private Boolean read;

    @Column(name = "sender_notified")
    private Boolean senderNotified;

    @Column(name = "content", length = -1)
    private String content;

    @Column(name = "conv_id", length = -1)
    private String convId;
}
