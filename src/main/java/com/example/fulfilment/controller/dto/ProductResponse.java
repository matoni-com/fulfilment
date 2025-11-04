package com.example.fulfilment.controller.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResponse {
  Long id;
  String merchantId;
  String warehouseId;
  String merchantSku;
  String manufacturerSku;
  String manufacturerName;
  String ean;
  String itemName;
  Boolean isActive;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
