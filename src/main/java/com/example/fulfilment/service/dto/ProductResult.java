package com.example.fulfilment.service.dto;

import java.time.LocalDateTime;

public record ProductResult(
    Long id,
    String merchantId,
    String warehouseId,
    String merchantSku,
    String manufacturerSku,
    String manufacturerName,
    String ean,
    String itemName,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
