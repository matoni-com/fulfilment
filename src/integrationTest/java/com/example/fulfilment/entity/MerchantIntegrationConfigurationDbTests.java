package com.example.fulfilment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.repository.MerchantFlowRepository;
import com.example.fulfilment.repository.MerchantIntegrationConfigurationRepository;
import com.example.fulfilment.repository.MerchantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MerchantIntegrationConfigurationDbTests extends BaseIntegrationSuite {

  @Autowired private MerchantIntegrationConfigurationRepository configRepository;
  @Autowired private MerchantRepository merchantRepository;
  @Autowired private MerchantFlowRepository merchantFlowRepository;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void cleanDatabase() {
    merchantFlowRepository.deleteAll();
    configRepository.deleteAll();
  }

  @Test
  @DisplayName("API_KEY connection should be stored and loaded polymorphically from DB")
  void apiKeyConnection_shouldPersistAndLoadFromDatabase() {
    // given
    Merchant merchant = getMerchant();

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    MerchantIntegrationConfiguration saved = configRepository.save(config);

    // when
    MerchantIntegrationConfiguration reloaded =
        configRepository.findById(saved.getId()).orElseThrow();

    // then
    assertThat(reloaded.getMerchant().getId()).isEqualTo(merchant.getId());
    assertThat(reloaded.getConnectionSettings()).isInstanceOf(ApiKeyConnection.class);

    ApiKeyConnection conn = (ApiKeyConnection) reloaded.getConnectionSettings();
    assertThat(conn.apiKey()).isEqualTo("secret123");
    assertThat(conn.url()).isEqualTo("https://api.example.com");
  }

  @Test
  @DisplayName("FTP connection should be stored and loaded polymorphically from DB")
  void ftpConnection_shouldPersistAndLoadFromDatabase() {
    // given
    Merchant merchant = getMerchant();

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setMerchant(merchant);
    config.setFtpConnection("ftp.example.com", "ftpuser", "ftppass", 21);

    MerchantIntegrationConfiguration saved = configRepository.save(config);

    // when
    MerchantIntegrationConfiguration reloaded =
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
  @DisplayName("connectionSettings should be stored as polymorphic JSON in DB")
  void connectionSettings_shouldBeStoredAsJsonInDatabase() throws Exception {
    // given
    Merchant merchant = getMerchant();

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    MerchantIntegrationConfiguration saved = configRepository.save(config);

    // when: read raw JSON from DB
    String rawJson =
        jdbcTemplate.queryForObject(
            "select connection_settings from merchant_integration_configurations where id = ?",
            String.class,
            saved.getId());

    // then: basic sanity check on raw JSON
    assertThat(rawJson).isNotNull();
    // optional, keep it loose:
    assertThat(rawJson).contains("API_KEY");

    // and *proper* check via deserialization
    ConnectionSettings fromDb = objectMapper.readValue(rawJson, ConnectionSettings.class);

    assertThat(fromDb).isInstanceOf(ApiKeyConnection.class);

    ApiKeyConnection conn = (ApiKeyConnection) fromDb;
    assertThat(conn.apiKey()).isEqualTo("secret123");
    assertThat(conn.url()).isEqualTo("https://api.example.com");
  }

  @Test
  @DisplayName("MerchantFlow enums should be stored and loaded correctly via JPA")
  void merchantFlow_enumsShouldPersistAndLoadFromDatabase() {
    // given
    Merchant merchant = getMerchant();

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    MerchantIntegrationConfiguration savedConfig = configRepository.save(config);

    MerchantFlow flow = new MerchantFlow();
    flow.setMerchantIntegrationConfiguration(savedConfig);
    flow.setFlowKind(FlowKind.PRODUCT_IMPORT);
    flow.setDirection(FlowDirection.IMPORT);
    flow.setExecutionMode(ExecutionMode.ACTIVE);
    flow.setSchedule("0 */5 * * * *");
    flow.setEnabled(true);
    flow.setNotes("Test flow");

    MerchantFlow savedFlow = merchantFlowRepository.save(flow);

    // when
    MerchantFlow reloaded = merchantFlowRepository.findById(savedFlow.getId()).orElseThrow();

    // then
    assertThat(reloaded.getFlowKind()).isEqualTo(FlowKind.PRODUCT_IMPORT);
    assertThat(reloaded.getDirection()).isEqualTo(FlowDirection.IMPORT);
    assertThat(reloaded.getExecutionMode()).isEqualTo(ExecutionMode.ACTIVE);
    assertThat(reloaded.getSchedule()).isEqualTo("0 */5 * * * *");
    assertThat(reloaded.getEnabled()).isTrue();
    assertThat(reloaded.getNotes()).isEqualTo("Test flow");
  }

  @Test
  @DisplayName("MerchantFlow enums should be stored as named enum values in DB")
  void merchantFlow_enumsShouldBeStoredAsNamedEnumsInDatabase() {
    // given
    Merchant merchant = getMerchant();

    MerchantIntegrationConfiguration config = new MerchantIntegrationConfiguration();
    config.setMerchant(merchant);
    config.setApiKeyConnection("secret123", "https://api.example.com");

    MerchantIntegrationConfiguration savedConfig = configRepository.save(config);

    MerchantFlow flow = new MerchantFlow();
    flow.setMerchantIntegrationConfiguration(savedConfig);
    flow.setFlowKind(FlowKind.SALES_ORDER);
    flow.setDirection(FlowDirection.EXPORT);
    flow.setExecutionMode(ExecutionMode.PASSIVE);
    flow.setEnabled(false);

    MerchantFlow savedFlow = merchantFlowRepository.save(flow);

    // when: read raw DB values
    var row =
        jdbcTemplate.queryForMap(
            "select flow_kind, direction, execution_mode from merchant_flows where id = ?",
            savedFlow.getId());

    // then
    assertThat(row.get("flow_kind")).isEqualTo("SALES_ORDER");
    assertThat(row.get("direction")).isEqualTo("EXPORT");
    assertThat(row.get("execution_mode")).isEqualTo("PASSIVE");
  }

  // -------------------------
  // Helpers
  // -------------------------

  private Merchant getMerchant() {
    return merchantRepository.findById("MT").orElseThrow();
  }
}
