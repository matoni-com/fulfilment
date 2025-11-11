package com.example.fulfilment.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MerchantFlowTests {

  @Test
  @DisplayName("Create ACTIVE merchant product import flow with schedule and timestamps")
  void createActiveMerchantProductImportFlow() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-1");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("m-config-1");
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret", "https://api.example.com");

    MerchantFlow flow = new MerchantFlow();
    flow.setId("flow-1");
    flow.setMerchantIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.PRODUCT_IMPORT);
    flow.setDirection(FlowDirection.IMPORT);
    flow.setExecutionMode(ExecutionMode.ACTIVE);
    flow.setSchedule("*/5 * * * *"); // every 5 minutes
    flow.setEnabled(true);

    LocalDateTime now = LocalDateTime.now();
    flow.setLastRunAt(now.minusMinutes(5));
    flow.setNextPlannedRunAt(now.plusMinutes(5));
    flow.setNotes("Runs every 5 minutes to import products");

    assertEquals("flow-1", flow.getId());
    assertEquals(config, flow.getMerchantIntegrationConfiguration());
    assertEquals(FlowKind.PRODUCT_IMPORT, flow.getFlowKind());
    assertEquals(FlowDirection.IMPORT, flow.getDirection());
    assertEquals(ExecutionMode.ACTIVE, flow.getExecutionMode());
    assertEquals("*/5 * * * *", flow.getSchedule());
    assertTrue(flow.getEnabled());
    assertNotNull(flow.getLastRunAt());
    assertNotNull(flow.getNextPlannedRunAt());
    assertEquals("Runs every 5 minutes to import products", flow.getNotes());
  }

  @Test
  @DisplayName("Create PASSIVE merchant stock update flow without schedule")
  void createPassiveMerchantStockUpdateFlow() {
    Merchant merchant = new Merchant();
    merchant.setId("merchant-2");

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setId("m-config-2");
    config.setMerchant(merchant);
    config.setUsernamePasswordConnection("user", "pass", "https://api.example.com");

    MerchantFlow flow = new MerchantFlow();
    flow.setId("flow-2");
    flow.setMerchantIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.STOCK_UPDATE);
    flow.setDirection(FlowDirection.EXPORT);
    flow.setExecutionMode(ExecutionMode.PASSIVE);
    flow.setSchedule(null); // passive -> no schedule
    flow.setEnabled(true);

    assertEquals(FlowKind.STOCK_UPDATE, flow.getFlowKind());
    assertEquals(FlowDirection.EXPORT, flow.getDirection());
    assertEquals(ExecutionMode.PASSIVE, flow.getExecutionMode());
    assertNull(flow.getSchedule());
  }
}


