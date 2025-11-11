package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests demonstrating Java 21 switch pattern matching with ConnectionSettings. This showcases the
 * sealed-trait-like pattern matching capabilities.
 */
class ConnectionSettingsPatternMatchingTests {

  @Test
  @DisplayName("Should use switch pattern matching to extract connection details (Java 21+)")
  void shouldUseSwitchPatternMatchingToExtractConnectionDetails() {
    ConnectionSettings apiKey = new ApiKeyConnection("secret123", "https://api.example.com");
    ConnectionSettings usernamePassword =
        new UsernamePasswordConnection("user1", "pass123", "https://api.example.com");
    ConnectionSettings ftp = new FtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    String apiKeyInfo = extractConnectionInfo(apiKey);
    String usernamePasswordInfo = extractConnectionInfo(usernamePassword);
    String ftpInfo = extractConnectionInfo(ftp);

    assertEquals("API Key connection to https://api.example.com", apiKeyInfo);
    assertEquals("Username/Password connection for user1 to https://api.example.com", usernamePasswordInfo);
    assertEquals("FTP connection to ftp.example.com:21 as ftpuser", ftpInfo);
  }

  @Test
  @DisplayName("Should use switch pattern matching to validate connection settings")
  void shouldUseSwitchPatternMatchingToValidateConnectionSettings() {
    ConnectionSettings validApiKey = new ApiKeyConnection("secret123", "https://api.example.com");
    ConnectionSettings invalidApiKey = new ApiKeyConnection("", "https://api.example.com");
    ConnectionSettings validFtp = new FtpConnection("ftp.example.com", "user", "pass", 21);
    ConnectionSettings invalidFtp = new FtpConnection("ftp.example.com", "user", "pass", null);

    assertTrue(isValid(validApiKey));
    assertFalse(isValid(invalidApiKey));
    assertTrue(isValid(validFtp));
    assertFalse(isValid(invalidFtp));
  }

  @Test
  @DisplayName("Should use switch pattern matching to get connection type name")
  void shouldUseSwitchPatternMatchingToGetConnectionTypeName() {
    ConnectionSettings apiKey = new ApiKeyConnection("secret", "https://api.example.com");
    ConnectionSettings usernamePassword =
        new UsernamePasswordConnection("user", "pass", "https://api.example.com");
    ConnectionSettings ftp = new FtpConnection("ftp.example.com", "user", "pass", 21);

    assertEquals("API_KEY", getConnectionType(apiKey));
    assertEquals("USERNAME_PASSWORD", getConnectionType(usernamePassword));
    assertEquals("FTP", getConnectionType(ftp));
  }

  @Test
  @DisplayName("Should handle null in switch pattern matching")
  void shouldHandleNullInSwitchPatternMatching() {
    ConnectionSettings nullSettings = null;

    String result = extractConnectionInfo(nullSettings);

    assertEquals("No connection settings provided", result);
  }

  private String extractConnectionInfo(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection conn -> {
        if (conn.getApiKey() == null || conn.getApiKey().isEmpty()) {
          yield "Invalid API Key connection: missing secret";
        }
        yield "API Key connection to " + conn.getUrl();
      }
      case UsernamePasswordConnection conn ->
          "Username/Password connection for " + conn.getUsername() + " to " + conn.getUrl();
      case FtpConnection conn ->
          "FTP connection to " + conn.getHost() + ":" + conn.getPort() + " as " + conn.getUsername();
      case null -> "No connection settings provided";
        default -> throw new IllegalStateException("Unexpected value: " + settings);
    };
  }

  private boolean isValid(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection conn ->
          conn.getApiKey() != null && !conn.getApiKey().isEmpty() && conn.getUrl() != null;
      case UsernamePasswordConnection conn ->
          conn.getUsername() != null
              && !conn.getUsername().isEmpty()
              && conn.getPassword() != null
              && !conn.getPassword().isEmpty()
              && conn.getUrl() != null;
      case FtpConnection conn ->
          conn.getHost() != null
              && !conn.getHost().isEmpty()
              && conn.getUsername() != null
              && !conn.getUsername().isEmpty()
              && conn.getPassword() != null
              && !conn.getPassword().isEmpty()
              && conn.getPort() != null
              && conn.getPort() > 0;
      case null -> false;
        default -> throw new IllegalStateException("Unexpected value: " + settings);
    };
  }

  private String getConnectionType(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection ignored -> "API_KEY";
      case UsernamePasswordConnection ignored -> "USERNAME_PASSWORD";
      case FtpConnection ignored -> "FTP";
        default -> throw new IllegalStateException("Unexpected value: " + settings);
    };
  }
}

