package com.example.fulfilment.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private String username;
  private String jwt;
  private List<String> merchantIds;
  private List<String> warehouseIds;

  public JwtAuthenticationToken(String jwt) {
    super(null);
    this.jwt = jwt;
    this.username = null;
    this.merchantIds = List.of();
    this.warehouseIds = List.of();
    setAuthenticated(false);
  }

  public JwtAuthenticationToken(
      String username,
      String jwt,
      Collection<? extends GrantedAuthority> authorities,
      List<String> merchantIds,
      List<String> warehouseIds) {
    super(authorities);
    this.jwt = jwt;
    this.username = username;
    this.merchantIds = merchantIds != null ? merchantIds : List.of();
    this.warehouseIds = warehouseIds != null ? warehouseIds : List.of();
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return jwt;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }
}
