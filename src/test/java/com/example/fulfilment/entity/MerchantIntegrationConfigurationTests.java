package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MerchantIntegrationConfigurationTests {

  @Test
  @DisplayName("Should create merchant integration configuration with API key connection")
  void shouldCreateMerchantIntegrationConfigurationWithApiKeyConnection() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("config-1");
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    assertEquals("config-1", config.getId());
    assertEquals(merchant, config.getMerchant());
    assertNotNull(config.getApiKeyConnection());
    assertEquals("secret123", config.getApiKeyConnection().getApiKey());
    assertEquals("https://api.example.com", config.getApiKeyConnection().getUrl());
  }

  @Test
  @DisplayName("Should create merchant integration configuration with username/password connection")
  void shouldCreateMerchantIntegrationConfigurationWithUsernamePasswordConnection() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("config-1");
    config.setMerchant(merchant);
    config.setUsernamePasswordConnection("user1", "pass123", "https://api.example.com");

    assertNotNull(config.getUsernamePasswordConnection());
    assertEquals("user1", config.getUsernamePasswordConnection().getUsername());
    assertEquals("pass123", config.getUsernamePasswordConnection().getPassword());
    assertEquals("https://api.example.com", config.getUsernamePasswordConnection().getUrl());
  }

  @Test
  @DisplayName("Should create merchant integration configuration with FTP connection")
  void shouldCreateMerchantIntegrationConfigurationWithFtpConnection() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("config-1");
    config.setMerchant(merchant);
    config.setFtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    assertNotNull(config.getFtpConnection());
    assertEquals("ftp.example.com", config.getFtpConnection().getHost());
    assertEquals("ftpuser", config.getFtpConnection().getUsername());
    assertEquals("ftppass", config.getFtpConnection().getPassword());
    assertEquals(21, config.getFtpConnection().getPort());
  }

  @Test
  @DisplayName("Should return null for wrong connection type when using getter methods")
  void shouldReturnNullForWrongConnectionType() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("config-1");
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret", "https://api.example.com");

    assertNull(config.getUsernamePasswordConnection());
    assertNull(config.getFtpConnection());
  }

  @Test
  @DisplayName("Should support pattern matching on connection settings (Java 21+)")
  void shouldSupportPatternMatchingOnConnectionSettings() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration apiKeyConfig = new MerchantIntegrationConfiguration();
    apiKeyConfig.setId("config-1");
    apiKeyConfig.setMerchant(merchant);
    apiKeyConfig.setApiKeyConnection("secret123", "https://api.example.com");

    String result = processConnectionSettings(apiKeyConfig.getConnectionSettings());

    assertEquals("API Key connection", result);
  }

  private String processConnectionSettings(ConnectionSettings settings) {
    return switch (settings) {
      case ApiKeyConnection ignored -> "API Key connection";
      case UsernamePasswordConnection ignored -> "Username/Password connection";
      case FtpConnection ignored -> "FTP connection";
      case null -> "No connection";
        default -> throw new IllegalStateException("Unexpected value: " + settings);
    };
  }
}

