package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WarehouseFlowTests {

  @Test
  @DisplayName("Create PASSIVE warehouse product import (export from system) flow without schedule")
  void createPassiveWarehouseProductImportFlow() {
    Warehouse wh = new Warehouse();
    wh.setId("wh-1");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("w-config-1");
    config.setWarehouse(wh);
    config.setFtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    WarehouseFlow flow = new WarehouseFlow();
    flow.setId("w-flow-1");
    flow.setWarehouseIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.PRODUCT_IMPORT);
    flow.setDirection(FlowDirection.EXPORT); // exporting products to warehouse
    flow.setExecutionMode(ExecutionMode.PASSIVE);
    flow.setSchedule(null);
    flow.setEnabled(true);
    flow.setNotes("Triggered immediately after merchant import completes");

    assertEquals(FlowKind.PRODUCT_IMPORT, flow.getFlowKind());
    assertEquals(FlowDirection.EXPORT, flow.getDirection());
    assertEquals(ExecutionMode.PASSIVE, flow.getExecutionMode());
    assertNull(flow.getSchedule());
    assertEquals("Triggered immediately after merchant import completes", flow.getNotes());
  }

  @Test
  @DisplayName("Create ACTIVE warehouse stock update (import to system) flow with schedule")
  void createActiveWarehouseStockUpdateFlow() {
    Warehouse wh = new Warehouse();
    wh.setId("wh-2");

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setId("w-config-2");
    config.setWarehouse(wh);
    config.setApiKeyConnection("secret", "https://warehouse.example.com");

    WarehouseFlow flow = new WarehouseFlow();
    flow.setId("w-flow-2");
    flow.setWarehouseIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.STOCK_UPDATE);
    flow.setDirection(FlowDirection.IMPORT);
    flow.setExecutionMode(ExecutionMode.ACTIVE);
    flow.setSchedule("PT10M"); // every 10 minutes via ISO-8601 duration
    flow.setEnabled(true);

    LocalDateTime now = LocalDateTime.now();
    flow.setLastRunAt(now.minusMinutes(10));
    flow.setNextPlannedRunAt(now.plusMinutes(10));

    assertEquals(FlowKind.STOCK_UPDATE, flow.getFlowKind());
    assertEquals(FlowDirection.IMPORT, flow.getDirection());
    assertEquals(ExecutionMode.ACTIVE, flow.getExecutionMode());
    assertEquals("PT10M", flow.getSchedule());
    assertNotNull(flow.getLastRunAt());
    assertNotNull(flow.getNextPlannedRunAt());
  }
}


