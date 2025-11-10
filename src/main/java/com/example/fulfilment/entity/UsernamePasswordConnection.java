package com.example.fulfilment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Connection settings for username/password authentication: username, password, and url
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class UsernamePasswordConnection extends ConnectionSettings {
  private String username;
  private String password;
  private String url;
}

