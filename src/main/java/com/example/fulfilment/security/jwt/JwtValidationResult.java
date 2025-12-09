package com.example.fulfilment.security.jwt;

import java.util.List;

public record JwtValidationResult(
    String username,
    List<String> authorities,
    List<String> merchantIds,
    List<String> warehouseIds) {}
