package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectionSettingsTests {

  @Test
  @DisplayName("ApiKeyConnection should store apiSecret and url")
  void apiKeyConnection_shouldStoreApiSecretAndUrl() {
    ApiKeyConnection connection = new ApiKeyConnection("secret123", "https://api.example.com");

    assertEquals("secret123", connection.getApiKey());
    assertEquals("https://api.example.com", connection.getUrl());
  }

  @Test
  @DisplayName("UsernamePasswordConnection should store username, password, and url")
  void usernamePasswordConnection_shouldStoreUsernamePasswordAndUrl() {
    UsernamePasswordConnection connection =
        new UsernamePasswordConnection("user1", "pass123", "https://api.example.com");

    assertEquals("user1", connection.getUsername());
    assertEquals("pass123", connection.getPassword());
    assertEquals("https://api.example.com", connection.getUrl());
  }

  @Test
  @DisplayName("FtpConnection should store host, username, password, and port")
  void ftpConnection_shouldStoreHostUsernamePasswordAndPort() {
    FtpConnection connection = new FtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    assertEquals("ftp.example.com", connection.getHost());
    assertEquals("ftpuser", connection.getUsername());
    assertEquals("ftppass", connection.getPassword());
    assertEquals(21, connection.getPort());
  }

  @Test
  @DisplayName("ConnectionSettings should support pattern matching with instanceof (Java 17+)")
  void connectionSettings_shouldSupportInstanceofPatternMatching() {
    ConnectionSettings apiKey = new ApiKeyConnection("secret", "https://api.example.com");
    ConnectionSettings usernamePassword =
        new UsernamePasswordConnection("user", "pass", "https://api.example.com");
    ConnectionSettings ftp = new FtpConnection("ftp.example.com", "user", "pass", 21);

    assertTrue(apiKey instanceof ApiKeyConnection);
    assertTrue(usernamePassword instanceof UsernamePasswordConnection);
    assertTrue(ftp instanceof FtpConnection);
  }

  @Test
  @DisplayName("ConnectionSettings should support pattern matching with switch (Java 21+)")
  void connectionSettings_shouldSupportSwitchPatternMatching() {
    ConnectionSettings apiKey = new ApiKeyConnection("secret123", "https://api.example.com");
    ConnectionSettings usernamePassword =
        new UsernamePasswordConnection("user1", "pass123", "https://api.example.com");
    ConnectionSettings ftp = new FtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    String apiKeyResult = processConnection(apiKey);
    String usernamePasswordResult = processConnection(usernamePassword);
    String ftpResult = processConnection(ftp);

    assertEquals("API Key: secret123 -> https://api.example.com", apiKeyResult);
    assertEquals("Username/Password: user1 -> https://api.example.com", usernamePasswordResult);
    assertEquals("FTP: ftpuser@ftp.example.com:21", ftpResult);
  }

  private String processConnection(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection conn ->
          "API Key: " + conn.getApiKey() + " -> " + conn.getUrl();
      case UsernamePasswordConnection conn ->
          "Username/Password: " + conn.getUsername() + " -> " + conn.getUrl();
      case FtpConnection conn ->
          "FTP: " + conn.getUsername() + "@" + conn.getHost() + ":" + conn.getPort();
      case null -> "No connection settings";
        default -> throw new IllegalStateException("Unexpected value: " + settings);
    };
  }
}

