package com.example.fulfilment.security;

import com.example.fulfilment.security.jwt.InvalidJwtException;
import com.example.fulfilment.security.jwt.JwtValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private JwtValidator jwtValidator;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String jwt = (String) authentication.getCredentials();

    try {
      var result = jwtValidator.validateToken(jwt);
      var grantedAuthorities =
          result.authorities().stream().map(SimpleGrantedAuthority::new).toList();

      return new JwtAuthenticationToken(
          result.username(), jwt, grantedAuthorities, result.merchantIds(), result.warehouseIds());
    } catch (InvalidJwtException e) {
      throw new BadCredentialsException("Invalid JWT", e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
