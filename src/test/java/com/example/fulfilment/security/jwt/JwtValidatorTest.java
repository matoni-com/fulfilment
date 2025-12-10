package com.example.fulfilment.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

public class JwtValidatorTest {

  private SecretKey secretKey = Jwts.SIG.HS256.key().build();
  private JwtValidator jwtValidator = new JwtValidator(secretKey);

  @Test
  public void validJwtWithAllClaims() throws InvalidJwtException {
    // Create JWT with all claims
    String jwt =
        Jwts.builder()
            .subject("testuser")
            .claim("authorities", List.of("AUTH1", "AUTH2"))
            .claim("merchantIds", List.of("M1", "M2"))
            .claim("warehouseIds", List.of("W1", "W2"))
            .signWith(secretKey)
            .compact();

    JwtValidationResult result = jwtValidator.validateToken(jwt);

    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.authorities()).containsExactly("AUTH1", "AUTH2");
    assertThat(result.merchantIds()).containsExactly("M1", "M2");
    assertThat(result.warehouseIds()).containsExactly("W1", "W2");
  }

  @Test
  public void validJwtMissingMerchantIds() throws InvalidJwtException {
    String jwt =
        Jwts.builder()
            .subject("testuser")
            .claim("authorities", List.of("AUTH1"))
            .claim("warehouseIds", List.of("W1"))
            .signWith(secretKey)
            .compact();

    JwtValidationResult result = jwtValidator.validateToken(jwt);

    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.authorities()).containsExactly("AUTH1");
    assertThat(result.merchantIds()).isEmpty();
    assertThat(result.warehouseIds()).containsExactly("W1");
  }

  @Test
  public void validJwtMissingWarehouseIds() throws InvalidJwtException {
    String jwt =
        Jwts.builder()
            .subject("testuser")
            .claim("authorities", List.of("AUTH1"))
            .claim("merchantIds", List.of("M1"))
            .signWith(secretKey)
            .compact();

    JwtValidationResult result = jwtValidator.validateToken(jwt);

    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.authorities()).containsExactly("AUTH1");
    assertThat(result.merchantIds()).containsExactly("M1");
    assertThat(result.warehouseIds()).isEmpty();
  }

  @Test
  public void validJwtMissingBothMerchantAndWarehouseIds() throws InvalidJwtException {
    String jwt =
        Jwts.builder()
            .subject("testuser")
            .claim("authorities", List.of("AUTH1"))
            .signWith(secretKey)
            .compact();

    JwtValidationResult result = jwtValidator.validateToken(jwt);

    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.authorities()).containsExactly("AUTH1");
    assertThat(result.merchantIds()).isEmpty();
    assertThat(result.warehouseIds()).isEmpty();
  }

  @Test
  public void validJwtMissingAuthorities() throws InvalidJwtException {
    String jwt =
        Jwts.builder()
            .subject("testuser")
            .claim("merchantIds", List.of("M1"))
            .claim("warehouseIds", List.of("W1"))
            .signWith(secretKey)
            .compact();

    JwtValidationResult result = jwtValidator.validateToken(jwt);

    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result.authorities()).isEmpty();
    assertThat(result.merchantIds()).containsExactly("M1");
    assertThat(result.warehouseIds()).containsExactly("W1");
  }

  @Test
  public void invalidJwtThrowsException() {
    assertThatThrownBy(() -> jwtValidator.validateToken("invalid_token"))
        .isInstanceOf(InvalidJwtException.class)
        .hasMessageContaining("Invalid JWT");
  }

  @Test
  public void jwtSignedWithDifferentKeyThrowsException() {
    SecretKey differentKey = Jwts.SIG.HS256.key().build();
    String jwt = Jwts.builder().subject("testuser").signWith(differentKey).compact();

    assertThatThrownBy(() -> jwtValidator.validateToken(jwt))
        .isInstanceOf(InvalidJwtException.class)
        .hasMessageContaining("Invalid JWT");
  }
}
