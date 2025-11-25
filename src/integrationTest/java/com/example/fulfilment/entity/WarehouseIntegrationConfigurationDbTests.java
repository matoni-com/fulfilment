package com.example.fulfilment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.repository.WarehouseFlowRepository;
import com.example.fulfilment.repository.WarehouseIntegrationConfigurationRepository;
import com.example.fulfilment.repository.WarehouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WarehouseIntegrationConfigurationDbTests extends BaseIntegrationSuite {

  @Autowired private WarehouseIntegrationConfigurationRepository configRepository;

  @Autowired private WarehouseRepository warehouseRepository;

  @Autowired private WarehouseFlowRepository warehouseFlowRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("API_KEY connection should be stored and loaded polymorphically from DB")
  void apiKeyConnection_shouldPersistAndLoadFromDatabase() {
    // given
    Warehouse warehouse = anyExistingWarehouse();

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setWarehouse(warehouse);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    WarehouseIntegrationConfiguration saved = configRepository.save(config);

    // when
    WarehouseIntegrationConfiguration reloaded =
        configRepository.findById(saved.getId()).orElseThrow();

    // then
    assertThat(reloaded.getWarehouse().getId()).isEqualTo(warehouse.getId());
    assertThat(reloaded.getConnectionSettings()).isInstanceOf(ApiKeyConnection.class);

    ApiKeyConnection conn = (ApiKeyConnection) reloaded.getConnectionSettings();
    assertThat(conn.apiKey()).isEqualTo("secret123");
    assertThat(conn.url()).isEqualTo("https://api.example.com");
  }

  @Test
  @DisplayName("FTP connection should be stored and loaded polymorphically from DB")
  void ftpConnection_shouldPersistAndLoadFromDatabase() {
    // given
    Warehouse warehouse = anyExistingWarehouse();

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setWarehouse(warehouse);
    config.setFtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    WarehouseIntegrationConfiguration saved = configRepository.save(config);

    // when
    WarehouseIntegrationConfiguration reloaded =
        configRepository.findById(saved.getId()).orElseThrow();

    // then
    assertThat(reloaded.getConnectionSettings()).isInstanceOf(FtpConnection.class);

    FtpConnection conn = (FtpConnection) reloaded.getConnectionSettings();
    assertThat(conn.host()).isEqualTo("ftp.example.com");
    assertThat(conn.username()).isEqualTo("ftpuser");
    assertThat(conn.password()).isEqualTo("ftppass");
    assertThat(conn.port()).isEqualTo(21);
  }

  @Test
  @DisplayName("connectionSettings should be stored as polymorphic JSON in DB (warehouse)")
  void connectionSettings_shouldBeStoredAsJsonInDatabase() throws Exception {
    // given
    Warehouse warehouse = anyExistingWarehouse();

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setWarehouse(warehouse);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    WarehouseIntegrationConfiguration saved = configRepository.save(config);

    // when: read raw JSON from DB
    String rawJson =
        jdbcTemplate.queryForObject(
            "select connection_settings from warehouse_integration_configurations where id = ?",
            String.class,
            saved.getId());

    // then: basic sanity check on raw JSON
    assertThat(rawJson).isNotNull();
    assertThat(rawJson).contains("API_KEY"); // loose check on discriminator

    // and proper check via deserialization
    ConnectionSettings fromDb = objectMapper.readValue(rawJson, ConnectionSettings.class);

    assertThat(fromDb).isInstanceOf(ApiKeyConnection.class);

    ApiKeyConnection conn = (ApiKeyConnection) fromDb;
    assertThat(conn.apiKey()).isEqualTo("secret123");
    assertThat(conn.url()).isEqualTo("https://api.example.com");
  }

  @Test
  @DisplayName("WarehouseFlow enums should be stored and loaded correctly via JPA")
  void warehouseFlow_enumsShouldPersistAndLoadFromDatabase() {
    // given: a warehouse integration config for some existing warehouse
    WarehouseIntegrationConfiguration config = createConfigForAnyWarehouse();

    WarehouseFlow flow = new WarehouseFlow();
    flow.setWarehouseIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.STOCK_UPDATE);
    flow.setDirection(FlowDirection.EXPORT);
    flow.setExecutionMode(ExecutionMode.ACTIVE);
    flow.setSchedule("0 */5 * * * *"); // every 5 minutes
    flow.setEnabled(true);
    flow.setNotes("Test STOCK_UPDATE export flow");

    WarehouseFlow saved = warehouseFlowRepository.save(flow);

    // when
    WarehouseFlow reloaded = warehouseFlowRepository.findById(saved.getId()).orElseThrow();

    // then
    assertThat(reloaded.getWarehouseIntegrationConfiguration().getId()).isEqualTo(config.getId());
    assertThat(reloaded.getFlowKind()).isEqualTo(FlowKind.STOCK_UPDATE);
    assertThat(reloaded.getDirection()).isEqualTo(FlowDirection.EXPORT);
    assertThat(reloaded.getExecutionMode()).isEqualTo(ExecutionMode.ACTIVE);
    assertThat(reloaded.getSchedule()).isEqualTo("0 */5 * * * *");
    assertThat(reloaded.getEnabled()).isTrue();
    assertThat(reloaded.getNotes()).isEqualTo("Test STOCK_UPDATE export flow");
  }

  @Test
  @DisplayName("WarehouseFlow enums should be stored as correct Postgres enums in DB")
  void warehouseFlow_enumsShouldBeStoredAsEnumTextInDatabase() {
    // given
    WarehouseIntegrationConfiguration config = createConfigForAnyWarehouse();

    WarehouseFlow flow = new WarehouseFlow();
    flow.setWarehouseIntegrationConfiguration(config);
    flow.setFlowKind(FlowKind.SALES_ORDER);
    flow.setDirection(FlowDirection.IMPORT);
    flow.setExecutionMode(ExecutionMode.PASSIVE);
    flow.setSchedule(null); // PASSIVE -> no schedule
    flow.setEnabled(false);
    flow.setNotes("Passive SALES_ORDER import");

    WarehouseFlow saved = warehouseFlowRepository.save(flow);

    // when: read raw enum values from Postgres
    String flowKind =
        jdbcTemplate.queryForObject(
            "select flow_kind::text from warehouse_flows where id = ?",
            String.class,
            saved.getId());

    String direction =
        jdbcTemplate.queryForObject(
            "select direction::text from warehouse_flows where id = ?",
            String.class,
            saved.getId());

    String executionMode =
        jdbcTemplate.queryForObject(
            "select execution_mode::text from warehouse_flows where id = ?",
            String.class,
            saved.getId());

    // then: verify they match your enum literals / DB type definition
    assertThat(flowKind).isEqualTo("SALES_ORDER");
    assertThat(direction).isEqualTo("IMPORT");
    assertThat(executionMode).isEqualTo("PASSIVE");
  }

  // -------------------------
  // Helpers
  // -------------------------

  private WarehouseIntegrationConfiguration createConfigForAnyWarehouse() {
    Warehouse warehouse = anyExistingWarehouse();

    WarehouseIntegrationConfiguration config = new WarehouseIntegrationConfiguration();
    config.setWarehouse(warehouse);
    // choose any connection settings type you like
    config.setApiKeyConnection("secret-warehouse-key", "https://warehouse.example.com");

    return configRepository.save(config);
  }

  private Warehouse anyExistingWarehouse() {
    return StreamSupport.stream(warehouseRepository.findAll().spliterator(), false)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No warehouses found in test DB"));
  }
}
