package com.example.fulfilment.controller;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.entity.Merchant;
import com.example.fulfilment.entity.User;
import com.example.fulfilment.entity.UserAuthority;
import com.example.fulfilment.entity.Warehouse;
import com.example.fulfilment.repository.MerchantRepository;
import com.example.fulfilment.repository.UserRepository;
import com.example.fulfilment.repository.WarehouseRepository;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

public class UserAuthenticationTests extends BaseIntegrationSuite {

  @Autowired private MockMvc mockMvc;
  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private MerchantRepository merchantRepository;
  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private SecretKey jwtSignatureKey;

  @BeforeAll
  public void populateUser() {
    // Fetch prepopulated merchant and warehouse
    Merchant merchant = merchantRepository.findById("MT").orElseThrow();
    Warehouse warehouse = warehouseRepository.findById("WH").orElseThrow();

    // Create user and associate with merchant and warehouse
    User user = new User("johndoe", passwordEncoder.encode("12345"));
    user.getAuthorities().add(UserAuthority.READ_PRODUCTS);
    user.getMerchants().add(merchant);
    user.getWarehouses().add(warehouse);

    userRepository.save(user);
  }

  @AfterAll
  public void cleanUser() {
    userRepository.deleteAll();
  }

  @Test
  public void authenticateWithValidCreds() throws Exception {
    String expectedResponse =
        """
            {
                "access_token": "${json-unit.any-string}",
                "token_type": "Bearer",
                "expires_in": 600
            }
            """;

    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "johndoe",
                    "password": "12345"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(json().isEqualTo(expectedResponse));
  }

  @Test
  public void authenticateWithInvalidPassword() throws Exception {
    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "johndoe",
                    "password": "invalid"
                }
                """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void authenticateWithNonExistingUser() throws Exception {
    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "notexist",
                    "password": "12345"
                }
                """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void jwtContainsMerchantAndWarehouseIds() throws Exception {
    var response =
        mockMvc
            .perform(
                post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                        "username": "johndoe",
                        "password": "12345"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

    String token = JsonPath.read(response.getResponse().getContentAsString(), "$.access_token");

    var claims =
        Jwts.parser().verifyWith(jwtSignatureKey).build().parseSignedClaims(token).getPayload();

    assertThat(claims.get("merchantIds", List.class)).containsExactly("MT");
    assertThat(claims.get("warehouseIds", List.class)).containsExactly("WH");
  }
}
