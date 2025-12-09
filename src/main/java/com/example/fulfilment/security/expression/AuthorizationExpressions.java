package com.example.fulfilment.security.expression;

import com.example.fulfilment.security.JwtAuthenticationToken;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authExpressions")
public class AuthorizationExpressions {

  public boolean hasAccessToMerchant(String merchantId) {
    if (merchantId == null) return false;

    JwtAuthenticationToken jwtAuth = getJwtAuthenticationToken();
    if (jwtAuth == null) return false;

    return jwtAuth.getMerchantIds() != null && jwtAuth.getMerchantIds().contains(merchantId);
  }

  public boolean hasAccessToWarehouse(String warehouseId) {
    if (warehouseId == null) return false;

    JwtAuthenticationToken jwtAuth = getJwtAuthenticationToken();
    if (jwtAuth == null) return false;

    return jwtAuth.getWarehouseIds() != null && jwtAuth.getWarehouseIds().contains(warehouseId);
  }

  public boolean hasAccessToAllMerchants(Collection<String> requestedMerchantIds) {
    if (requestedMerchantIds == null) return false;

    JwtAuthenticationToken jwtAuth = getJwtAuthenticationToken();
    if (jwtAuth == null) return false;

    return jwtAuth.getMerchantIds() != null
        && jwtAuth.getMerchantIds().containsAll(requestedMerchantIds);
  }

  public boolean hasAccessToAllWarehouses(Collection<String> requestedWarehouseIds) {
    if (requestedWarehouseIds == null) return false;

    JwtAuthenticationToken jwtAuth = getJwtAuthenticationToken();
    if (jwtAuth == null) return false;

    return jwtAuth.getWarehouseIds() != null
        && jwtAuth.getWarehouseIds().containsAll(requestedWarehouseIds);
  }

  private JwtAuthenticationToken getJwtAuthenticationToken() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof JwtAuthenticationToken jwtAuth)) return null;
    return jwtAuth;
  }
}
