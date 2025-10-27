package com.example.fulfilment.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.List;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

  private JwtParser parser;

  public JwtValidator(SecretKey jwtSignatureKey) {
    parser = Jwts.parser().verifyWith(jwtSignatureKey).build();
  }

  public Pair<String, List<String>> validateToken(String jwt) throws InvalidJwtException {
    Claims claims;

    try {
      claims = parser.parseSignedClaims(jwt).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidJwtException("Invalid JWT", e);
    }

    String username = claims.getSubject();

    List<String> authorities = (List<String>) claims.get("authorities", List.class);

    return Pair.of(username, authorities);
  }
}
