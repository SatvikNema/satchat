package com.satvik.satchat.entity;

import com.satvik.satchat.model.ERole;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
  @Id
  @Column(name = "id", nullable = false, columnDefinition = "uuid")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;
}
