package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectionSettingsTests {

  private final ObjectMapper objectMapper = new ObjectMapper();

  // ==========================
  //  JSON (de)serialization
  // ==========================

  @Test
  @DisplayName("ApiKeyConnection should serialize and deserialize as polymorphic JSON")
  void apiKeyConnection_shouldSerializeAndDeserializePolymorphicJson() throws Exception {
    ConnectionSettings original =
            new ApiKeyConnection("secret123", "https://api.example.com");

    String json = objectMapper.writeValueAsString(original);

    // Basic sanity checks on the JSON payload
    assertTrue(json.contains("\"type\":\"API_KEY\""));
    assertTrue(json.contains("\"apiKey\":\"secret123\""));
    assertTrue(json.contains("\"url\":\"https://api.example.com\""));

    ConnectionSettings deserialized =
            objectMapper.readValue(json, ConnectionSettings.class);

    assertInstanceOf(ApiKeyConnection.class, deserialized);
    ApiKeyConnection conn = (ApiKeyConnection) deserialized;
    assertEquals("secret123", conn.apiKey());
    assertEquals("https://api.example.com", conn.url());
  }

  @Test
  @DisplayName("UsernamePasswordConnection should serialize and deserialize as polymorphic JSON")
  void usernamePasswordConnection_shouldSerializeAndDeserializePolymorphicJson() throws Exception {
    ConnectionSettings original =
            new UsernamePasswordConnection("user1", "pass123", "https://api.example.com");

    String json = objectMapper.writeValueAsString(original);

    assertTrue(json.contains("\"type\":\"USERNAME_PASSWORD\""));
    assertTrue(json.contains("\"username\":\"user1\""));
    assertTrue(json.contains("\"password\":\"pass123\""));
    assertTrue(json.contains("\"url\":\"https://api.example.com\""));

    ConnectionSettings deserialized =
            objectMapper.readValue(json, ConnectionSettings.class);

    assertInstanceOf(UsernamePasswordConnection.class, deserialized);
    UsernamePasswordConnection conn = (UsernamePasswordConnection) deserialized;
    assertEquals("user1", conn.username());
    assertEquals("pass123", conn.password());
    assertEquals("https://api.example.com", conn.url());
  }

  @Test
  @DisplayName("FtpConnection should serialize and deserialize as polymorphic JSON")
  void ftpConnection_shouldSerializeAndDeserializePolymorphicJson() throws Exception {
    ConnectionSettings original =
            new FtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    String json = objectMapper.writeValueAsString(original);

    assertTrue(json.contains("\"type\":\"FTP\""));
    assertTrue(json.contains("\"host\":\"ftp.example.com\""));
    assertTrue(json.contains("\"username\":\"ftpuser\""));
    assertTrue(json.contains("\"password\":\"ftppass\""));
    assertTrue(json.contains("\"port\":21"));

    ConnectionSettings deserialized =
            objectMapper.readValue(json, ConnectionSettings.class);

    assertInstanceOf(FtpConnection.class, deserialized);
    FtpConnection conn = (FtpConnection) deserialized;
    assertEquals("ftp.example.com", conn.host());
    assertEquals("ftpuser", conn.username());
    assertEquals("ftppass", conn.password());
    assertEquals(21, conn.port());
  }

  // ==========================
  //  Pattern-matching behavior
  // ==========================

  @Test
  @DisplayName("ConnectionSettings switch should produce human-readable description")
  void connectionSettings_switchShouldProduceHumanReadableDescription() {
    ConnectionSettings apiKey =
            new ApiKeyConnection("secret123", "https://api.example.com");
    ConnectionSettings usernamePassword =
            new UsernamePasswordConnection("user1", "pass123", "https://api.example.com");
    ConnectionSettings ftp =
            new FtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    String apiKeyResult = describe(apiKey);
    String usernamePasswordResult = describe(usernamePassword);
    String ftpResult = describe(ftp);
    String nullResult = describe(null);

    assertEquals("API Key: secret123 -> https://api.example.com", apiKeyResult);
    assertEquals("Username/Password: user1 -> https://api.example.com", usernamePasswordResult);
    assertEquals("FTP: ftpuser@ftp.example.com:21", ftpResult);
    assertEquals("No connection settings", nullResult);
  }

  private String describe(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection conn ->
              "API Key: " + conn.apiKey() + " -> " + conn.url();
      case UsernamePasswordConnection conn ->
              "Username/Password: " + conn.username() + " -> " + conn.url();
      case FtpConnection conn ->
              "FTP: " + conn.username() + "@" + conn.host() + ":" + conn.port();
      case null -> "No connection settings";
    };
  }

  // ==========================
  //  Record value semantics
  // ==========================

  @Test
  @DisplayName("Records should have value-based equality")
  void records_shouldHaveValueBasedEquality() {
    ApiKeyConnection c1 = new ApiKeyConnection("secret123", "https://api.example.com");
    ApiKeyConnection c2 = new ApiKeyConnection("secret123", "https://api.example.com");
    ApiKeyConnection c3 = new ApiKeyConnection("other", "https://api.example.com");

    assertEquals(c1, c2);
    assertEquals(c1.hashCode(), c2.hashCode());
    assertNotEquals(c1, c3);
  }
}
