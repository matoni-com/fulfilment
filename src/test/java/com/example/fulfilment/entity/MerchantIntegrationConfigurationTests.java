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

    ConnectionSettings settings = config.getConnectionSettings();
    assertNotNull(settings);

    switch (settings) {
      case ApiKeyConnection api -> {
        assertEquals("secret123", api.getApiKey());
        assertEquals("https://api.example.com", api.getUrl());
      }
      default -> fail("Expected ApiKeyConnection but got: " + settings.getClass().getSimpleName());
    }
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

    ConnectionSettings settings = config.getConnectionSettings();
    assertNotNull(settings);

    switch (settings) {
      case UsernamePasswordConnection up -> {
        assertEquals("user1", up.getUsername());
        assertEquals("pass123", up.getPassword());
        assertEquals("https://api.example.com", up.getUrl());
      }
      default -> fail("Expected UsernamePasswordConnection but got: " + settings.getClass().getSimpleName());
    }
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

    ConnectionSettings settings = config.getConnectionSettings();
    assertNotNull(settings);

    switch (settings) {
      case FtpConnection ftp -> {
        assertEquals("ftp.example.com", ftp.getHost());
        assertEquals("ftpuser", ftp.getUsername());
        assertEquals("ftppass", ftp.getPassword());
        assertEquals(21, ftp.getPort());
      }
      default -> fail("Expected FtpConnection but got: " + settings.getClass().getSimpleName());
    }
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
    if (settings == null) {
      return "No connection";
    }

    // No default branch needed because ConnectionSettings is sealed
    return switch (settings) {
      case ApiKeyConnection ignored -> "API Key connection";
      case UsernamePasswordConnection ignored -> "Username/Password connection";
      case FtpConnection ignored -> "FTP connection";
    };
  }
}
