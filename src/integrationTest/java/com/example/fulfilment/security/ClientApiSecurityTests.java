package com.example.fulfilment.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.entity.*;
import com.example.fulfilment.repository.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

public class ClientApiSecurityTests extends BaseIntegrationSuite {

  @Autowired private ClientRepository clientRepository;
  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired private ProductRepository productRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired JdbcTemplate jdbcTemplate;

  @BeforeAll
  public void populateDatabase() {

    var client = new Client();
    client.setApiKey("mt-key");
    client.setApiSecret(passwordEncoder.encode("mt-secret"));
    client.setRole(ClientRole.MERCHANT);
    clientRepository.save(client);

    jdbcTemplate.update(
        "INSERT INTO merchants_clients (merchant_id, client_id) VALUES (?, ?)",
        "MT",
        client.getId());

    var client2 = new Client();
    client2.setApiKey("wh-key");
    client2.setApiSecret(passwordEncoder.encode("wh-secret"));
    client2.setRole(ClientRole.WAREHOUSE);
    clientRepository.save(client2);

    jdbcTemplate.update(
        "INSERT INTO warehouses_clients (warehouse_id, client_id) VALUES (?, ?)",
        "WH",
        client2.getId());

    Product product = new Product();
    product.setMerchantId("MT");
    product.setWarehouseId("WH");
    product.setMerchantSku("sku-001");
    product.setItemName("Test Product");
    product.setIsActive(true);

    productRepository.save(product);
  }

  @AfterAll
  public void cleanDatabase() {
    productRepository.deleteAll();

    jdbcTemplate.update("DELETE FROM merchants_clients");
    jdbcTemplate.update("DELETE FROM warehouses_clients");

    clientRepository.deleteAll();
  }

  @Test
  public void fakeCredsToEndpointRejected() throws Exception {
    String encoded =
        Base64.getEncoder()
            .encodeToString(("mt-key" + ":" + "fake-secret").getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(get("/api/merchant/products").header("Authorization", "Basic " + encoded))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or missing API key/secret"));
  }

  @Test
  public void unauthenticatedRequestToAuthenticatedEndpointRejected() throws Exception {

    mockMvc
        .perform(get("/api/merchant/products"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or missing API key/secret"));
  }

  @Test
  public void authenticatedRequestWithWarehouseRoleToMerchantAuthorizedEndpointRejected()
      throws Exception {
    String encoded =
        Base64.getEncoder()
            .encodeToString(("wh-key" + ":" + "wh-secret").getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(get("/api/merchant/products").header("Authorization", "Basic " + encoded))
        .andExpect(status().isForbidden())
        .andExpect(content().string("Access denied"));
  }

  @Test
  public void authenticatedRequestWithMerchantRoleToMerchantAuthorizedEndpointAccepted()
      throws Exception {
    String encoded =
        Base64.getEncoder()
            .encodeToString(("mt-key" + ":" + "mt-secret").getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(get("/api/merchant/products").header("Authorization", "Basic " + encoded))
        .andExpect(status().isOk())
        // this check makes sure that the request attribute merchantId is populated correctly
        .andExpect(
            content()
                .json(
                    """
                        [
                            {
                                "merchantId": "MT",
                                "warehouseId": "WH",
                                "merchantSku": "sku-001",
                                "itemName": "Test Product",
                                "isActive": true
                            }
                        ]
                        """));
  }
}
