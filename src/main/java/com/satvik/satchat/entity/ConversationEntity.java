package com.satvik.satchat.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "conversation", schema = "public", catalog = "postgres")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ConversationEntity {
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

  @Column(name = "content", length = -1)
  private String content;
}
