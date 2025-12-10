package com.example.fulfilment.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

  private JwtParser parser;

  public JwtValidator(SecretKey jwtSignatureKey) {
    parser = Jwts.parser().verifyWith(jwtSignatureKey).build();
  }

  public JwtValidationResult validateToken(String jwt) throws InvalidJwtException {
    Claims claims;

    try {
      claims = parser.parseSignedClaims(jwt).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidJwtException("Invalid JWT", e);
    }

    String username = claims.getSubject();

    List<String> authorities = (List<String>) claims.get("authorities", List.class);
    List<String> merchantIds = (List<String>) claims.get("merchantIds", List.class);
    List<String> warehouseIds = (List<String>) claims.get("warehouseIds", List.class);

    // Handle nulls - return empty lists
    if (authorities == null) authorities = List.of();
    if (merchantIds == null) merchantIds = List.of();
    if (warehouseIds == null) warehouseIds = List.of();

    return new JwtValidationResult(username, authorities, merchantIds, warehouseIds);
  }
}
