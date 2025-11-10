package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WarehouseIntegrationConfigurationTests {

  @Test
  @DisplayName("Should create warehouse integration configuration with API key connection")
  void shouldCreateWarehouseIntegrationConfigurationWithApiKeyConnection() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("config-1");
    config.setWarehouse(warehouse);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    assertEquals("config-1", config.getId());
    assertEquals(warehouse, config.getWarehouse());
    assertNotNull(config.getApiKeyConnection());
    assertEquals("secret123", config.getApiKeyConnection().getApiSecret());
    assertEquals("https://api.example.com", config.getApiKeyConnection().getUrl());
  }

  @Test
  @DisplayName("Should create warehouse integration configuration with username/password connection")
  void shouldCreateWarehouseIntegrationConfigurationWithUsernamePasswordConnection() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("config-1");
    config.setWarehouse(warehouse);
    config.setUsernamePasswordConnection("user1", "pass123", "https://api.example.com");

    assertNotNull(config.getUsernamePasswordConnection());
    assertEquals("user1", config.getUsernamePasswordConnection().getUsername());
    assertEquals("pass123", config.getUsernamePasswordConnection().getPassword());
    assertEquals("https://api.example.com", config.getUsernamePasswordConnection().getUrl());
  }

  @Test
  @DisplayName("Should create warehouse integration configuration with FTP connection")
  void shouldCreateWarehouseIntegrationConfigurationWithFtpConnection() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("config-1");
    config.setWarehouse(warehouse);
    config.setFtpConnection("ftp.example.com", "ftpuser", "ftppass", 22);

    assertNotNull(config.getFtpConnection());
    assertEquals("ftp.example.com", config.getFtpConnection().getHost());
    assertEquals("ftpuser", config.getFtpConnection().getUsername());
    assertEquals("ftppass", config.getFtpConnection().getPassword());
    assertEquals(22, config.getFtpConnection().getPort());
  }

  @Test
  @DisplayName("Should return null for wrong connection type when using getter methods")
  void shouldReturnNullForWrongConnectionType() {
    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("config-1");
    config.setWarehouse(warehouse);
    config.setFtpConnection("ftp.example.com", "user", "pass", 21);

    assertNull(config.getApiKeyConnection());
    assertNull(config.getUsernamePasswordConnection());
  }
}

