package com.example.fulfilment.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class WebUserDetails extends User {
  private final List<String> merchantIds;
  private final List<String> warehouseIds;

  public WebUserDetails(
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      List<String> merchantIds,
      List<String> warehouseIds) {
    super(username, password, authorities);
    this.merchantIds = merchantIds;
    this.warehouseIds = warehouseIds;
  }
}
