package com.example.fulfilment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Connection settings for FTP authentication: host, username, password, and port
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class FtpConnection extends ConnectionSettings {
  private String host;
  private String username;
  private String password;
  private Integer port;
}

