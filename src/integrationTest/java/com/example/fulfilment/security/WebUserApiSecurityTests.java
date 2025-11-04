package com.example.fulfilment.security;

import static com.jayway.jsonpath.JsonPath.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.entity.Authority;
import com.example.fulfilment.entity.User;
import com.example.fulfilment.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

public class WebUserApiSecurityTests extends BaseIntegrationSuite {

  @Autowired private UserRepository userRepository;

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  @Autowired private MockMvc mockMvc;

  @BeforeAll
  public void populateUsers() {
    User userWithWrongAuthority = new User("user1", passwordEncoder.encode("12345"));
    userWithWrongAuthority.addAuthority(new Authority("SOME_AUTHORITY"));

    User userWithHello2Authority = new User("user2", passwordEncoder.encode("23456"));
    userWithHello2Authority.addAuthority(new Authority("HELLO2"));

    User userWithoutAnyAuthority = new User("user3", passwordEncoder.encode("34567"));

    userRepository.save(userWithWrongAuthority);
    userRepository.save(userWithHello2Authority);
    userRepository.save(userWithoutAnyAuthority);
  }

  @AfterAll
  public void cleanUsers() {
    userRepository.deleteAll();
  }

  @Test
  public void fakeTokenToEndpointRejected() throws Exception {
    mockMvc
        .perform(get("/hello").header("Authorization", "Bearer fakeToken"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or missing token"));
  }

  @Test
  public void unauthenticatedRequestToAuthenticatedEndpointRejected() throws Exception {
    mockMvc
        .perform(get("/hello"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or missing token"));
  }

  @Test
  public void authenticatedRequestToAuthenticatedEndpointAccepted() throws Exception {
    var body =
        mockMvc
            .perform(
                post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "username": "user3",
                            "password": "34567"
                        }
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = read(body, "$.access_token");

    mockMvc
        .perform(get("/hello").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World!"));
  }

  @Test
  public void unauthenticatedRequestToAuthorizedEndpointRejected() throws Exception {
    mockMvc
        .perform(get("/hello2"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or missing token"));
  }

  @Test
  public void authenticatedRequestWithoutCorrectAuthorityToAuthorizedEndpointRejected()
      throws Exception {
    var body =
        mockMvc
            .perform(
                post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "username": "user1",
                            "password": "12345"
                        }
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = read(body, "$.access_token");

    mockMvc
        .perform(get("/hello2").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden())
        .andExpect(content().string("Access denied"));
  }

  @Test
  public void authenticatedRequestWithAuthorityToAuthorizedEndpointAccepted() throws Exception {
    var body =
        mockMvc
            .perform(
                post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "username": "user2",
                            "password": "23456"
                        }
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = read(body, "$.access_token");

    mockMvc
        .perform(get("/hello2").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World!"));
  }
}
