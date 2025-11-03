package com.example.fulfilment.security.filters;

import com.example.fulfilment.repository.MerchantClientRepository;
import com.example.fulfilment.repository.WarehouseClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/*Filters should not be beans, because in that case they are automatically started as a servlet filter by Spring
in addition to being explicitly started as a part of the security filter chain. This would lead to issues like the
filter being executed for endpoints that it was not explicitly registered for or being executed twice for endpoints
that it was registered for.*/
@AllArgsConstructor
public class MerchantAndWarehouseRequestAttributePopulator extends OncePerRequestFilter {

  private MerchantClientRepository merchantClientRepository;
  private WarehouseClientRepository warehouseClientRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated()) {
      if (AuthorityAuthorizationManager.hasRole("MERCHANT").check(() -> auth, null).isGranted())
        merchantClientRepository
            .findByApiKey(auth.getName())
            .ifPresent(
                merchantClient ->
                    request.setAttribute("merchantId", merchantClient.getMerchant().getId()));
      else if (AuthorityAuthorizationManager.hasRole("WAREHOUSE")
          .check(() -> auth, null)
          .isGranted())
        warehouseClientRepository
            .findByApiKey(auth.getName())
            .ifPresent(
                warehouseClient ->
                    request.setAttribute("warehouseId", warehouseClient.getWarehouse().getId()));
    }

    filterChain.doFilter(request, response);
  }
}
