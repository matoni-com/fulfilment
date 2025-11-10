package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MerchantWarehouseIntegrationConnectionTests {

  @Test
  @DisplayName("Should create connection between merchant and warehouse integration configurations")
  void shouldCreateConnectionBetweenMerchantAndWarehouseIntegrationConfigurations() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    MerchantIntegrationConfiguration merchantConfig = new MerchantIntegrationConfiguration();
    merchantConfig.setId("merchant-config-1");
    merchantConfig.setMerchant(merchant);
    merchantConfig.setApiKeyConnection("merchant-secret", "https://merchant-api.example.com");

    WarehouseIntegrationConfiguration warehouseConfig = new WarehouseIntegrationConfiguration();
    warehouseConfig.setId("warehouse-config-1");
    warehouseConfig.setWarehouse(warehouse);
    warehouseConfig.setApiKeyConnection("warehouse-secret", "https://warehouse-api.example.com");

    MerchantWarehouseIntegrationConnection connection =
        new MerchantWarehouseIntegrationConnection();
    connection.setId("connection-1");
    connection.setMerchantIntegrationConfiguration(merchantConfig);
    connection.setWarehouseIntegrationConfiguration(warehouseConfig);

    assertEquals("connection-1", connection.getId());
    assertEquals(merchantConfig, connection.getMerchantIntegrationConfiguration());
    assertEquals(warehouseConfig, connection.getWarehouseIntegrationConfiguration());
  }

  @Test
  @DisplayName("Should allow multiple connections from one merchant integration to different warehouses")
  void shouldAllowMultipleConnectionsFromOneMerchantIntegration() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    Warehouse warehouse1 = new Warehouse();
    warehouse1.setId("warehouse-1");

    Warehouse warehouse2 = new Warehouse();
    warehouse2.setId("warehouse-2");

    MerchantIntegrationConfiguration merchantConfig = new MerchantIntegrationConfiguration();
    merchantConfig.setId("merchant-config-1");
    merchantConfig.setMerchant(merchant);
    merchantConfig.setApiKeyConnection("secret", "https://api.example.com");

    WarehouseIntegrationConfiguration warehouseConfig1 = new WarehouseIntegrationConfiguration();
    warehouseConfig1.setId("warehouse-config-1");
    warehouseConfig1.setWarehouse(warehouse1);
    warehouseConfig1.setApiKeyConnection("secret1", "https://warehouse1.example.com");

    WarehouseIntegrationConfiguration warehouseConfig2 = new WarehouseIntegrationConfiguration();
    warehouseConfig2.setId("warehouse-config-2");
    warehouseConfig2.setWarehouse(warehouse2);
    warehouseConfig2.setApiKeyConnection("secret2", "https://warehouse2.example.com");

    MerchantWarehouseIntegrationConnection connection1 =
        new MerchantWarehouseIntegrationConnection();
    connection1.setId("connection-1");
    connection1.setMerchantIntegrationConfiguration(merchantConfig);
    connection1.setWarehouseIntegrationConfiguration(warehouseConfig1);

    MerchantWarehouseIntegrationConnection connection2 =
        new MerchantWarehouseIntegrationConnection();
    connection2.setId("connection-2");
    connection2.setMerchantIntegrationConfiguration(merchantConfig);
    connection2.setWarehouseIntegrationConfiguration(warehouseConfig2);

    assertEquals(merchantConfig, connection1.getMerchantIntegrationConfiguration());
    assertEquals(warehouseConfig1, connection1.getWarehouseIntegrationConfiguration());

    assertEquals(merchantConfig, connection2.getMerchantIntegrationConfiguration());
    assertEquals(warehouseConfig2, connection2.getWarehouseIntegrationConfiguration());
  }

  @Test
  @DisplayName("Should allow multiple connections from one warehouse integration to different merchants")
  void shouldAllowMultipleConnectionsFromOneWarehouseIntegration() {
    Merchant merchant1 = new Merchant();
    merchant1.setId("merchant-1");

    Merchant merchant2 = new Merchant();
    merchant2.setId("merchant-2");

    Warehouse warehouse = new Warehouse();
    warehouse.setId("warehouse-1");

    MerchantIntegrationConfiguration merchantConfig1 = new MerchantIntegrationConfiguration();
    merchantConfig1.setId("merchant-config-1");
    merchantConfig1.setMerchant(merchant1);
    merchantConfig1.setApiKeyConnection("secret1", "https://merchant1.example.com");

    MerchantIntegrationConfiguration merchantConfig2 = new MerchantIntegrationConfiguration();
    merchantConfig2.setId("merchant-config-2");
    merchantConfig2.setMerchant(merchant2);
    merchantConfig2.setApiKeyConnection("secret2", "https://merchant2.example.com");

    WarehouseIntegrationConfiguration warehouseConfig = new WarehouseIntegrationConfiguration();
    warehouseConfig.setId("warehouse-config-1");
    warehouseConfig.setWarehouse(warehouse);
    warehouseConfig.setApiKeyConnection("warehouse-secret", "https://warehouse.example.com");

    MerchantWarehouseIntegrationConnection connection1 =
        new MerchantWarehouseIntegrationConnection();
    connection1.setId("connection-1");
    connection1.setMerchantIntegrationConfiguration(merchantConfig1);
    connection1.setWarehouseIntegrationConfiguration(warehouseConfig);

    MerchantWarehouseIntegrationConnection connection2 =
        new MerchantWarehouseIntegrationConnection();
    connection2.setId("connection-2");
    connection2.setMerchantIntegrationConfiguration(merchantConfig2);
    connection2.setWarehouseIntegrationConfiguration(warehouseConfig);

    assertEquals(merchantConfig1, connection1.getMerchantIntegrationConfiguration());
    assertEquals(warehouseConfig, connection1.getWarehouseIntegrationConfiguration());

    assertEquals(merchantConfig2, connection2.getMerchantIntegrationConfiguration());
    assertEquals(warehouseConfig, connection2.getWarehouseIntegrationConfiguration());
  }
}

