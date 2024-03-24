package com.satvik.satchat.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDeliveryStatusUpdate {
  private UUID id;
  private String content;
  private MessageDeliveryStatusEnum messageDeliveryStatusEnum;
}
