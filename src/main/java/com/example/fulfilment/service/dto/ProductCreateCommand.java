package com.example.fulfilment.service.dto;

public record ProductCreateCommand(
    String merchantId,
    String warehouseId,
    String merchantSku,
    String manufacturerSku,
    String manufacturerName,
    String ean,
    String itemName,
    Boolean isActive) {}
