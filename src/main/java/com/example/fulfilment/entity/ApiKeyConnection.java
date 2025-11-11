package com.example.fulfilment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Connection settings for API key authentication: apiKey and url
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class ApiKeyConnection extends ConnectionSettings {
  private String apiKey;
  private String url;
}

