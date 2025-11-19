package com.example.fulfilment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
    @NotBlank(message = "Warehouse ID is required") String warehouseId,
    @NotBlank(message = "Merchant SKU is required")
        @Size(max = 50, message = "Merchant SKU must not exceed 50 characters")
        String merchantSku,
    @NotBlank(message = "Manufacturer SKU is required") String manufacturerSku,
    @NotBlank(message = "Manufacturer Name is required") String manufacturerName,
    @NotBlank(message = "EAN is required")
        @Pattern(regexp = "\\d{13}", message = "EAN must be a 13-digit number")
        String ean,
    @NotBlank(message = "Item Name is required")
        @Size(max = 100, message = "Item Name must not exceed 100 characters")
        String itemName,
    Boolean isActive) {}
