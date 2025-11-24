package com.example.fulfilment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.repository.AddressRepository;
import com.example.fulfilment.repository.MerchantIntegrationConfigurationRepository;
import com.example.fulfilment.repository.MerchantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MerchantIntegrationConfigurationDbTests extends BaseIntegrationSuite {

  @Autowired private MerchantIntegrationConfigurationRepository configRepository;
  @Autowired private MerchantRepository merchantRepository;
  @Autowired private AddressRepository addressRepository;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("API_KEY connection should be stored and loaded polymorphically from DB")
  void apiKeyConnection_shouldPersistAndLoadFromDatabase() {
    // given
    Merchant merchant = createValidMerchant("MT_DE_2000");

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
    Merchant merchant = createValidMerchant("MT_DE_2001");

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
    Merchant merchant = createValidMerchant("MT_DE_2002");

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

  // -------------------------
  // Helpers
  // -------------------------

  private Merchant createValidMerchant(String id) {
    Address address = new Address();
    address.setStreet("Test Street");
    address.setHouseNumber("2");
    address.setCity("Berlin");
    address.setPostbox("10115");
    address.setCountry("DE");
    address.setZip("1000");

    Address savedAddress = addressRepository.save(address);

    Merchant merchant = new Merchant();
    merchant.setId(id);
    merchant.setCompanyName("Test Merchant");
    merchant.setAddress(address);

    return merchantRepository.save(merchant);
  }
}
