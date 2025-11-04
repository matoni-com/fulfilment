package com.example.fulfilment.security.filters;

import static org.mockito.Mockito.*;

import com.example.fulfilment.entity.Merchant;
import com.example.fulfilment.entity.MerchantClient;
import com.example.fulfilment.entity.Warehouse;
import com.example.fulfilment.entity.WarehouseClient;
import com.example.fulfilment.repository.MerchantClientRepository;
import com.example.fulfilment.repository.WarehouseClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class MerchantAndWarehouseRequestAttributePopulatorTests {

  @Mock MerchantClientRepository merchantClientRepository;
  @Mock WarehouseClientRepository warehouseClientRepository;
  @InjectMocks MerchantAndWarehouseRequestAttributePopulator populator;

  HttpServletRequest request = mock(HttpServletRequest.class);
  HttpServletResponse response = mock(HttpServletResponse.class);
  FilterChain chain = mock(FilterChain.class);

  @AfterEach
  public void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void filterPopulatesMerchantIdRequestAttributeForAuthorizedMerchantRequests()
      throws ServletException, IOException {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "api_key", null, List.of(new SimpleGrantedAuthority("ROLE_MERCHANT"))));

    var merchant = new Merchant();
    merchant.setId("merchant_id");
    var merchantClient = new MerchantClient();
    merchantClient.setMerchant(merchant);
    when(merchantClientRepository.findByApiKey("api_key")).thenReturn(Optional.of(merchantClient));

    populator.doFilter(request, response, chain);

    verify(request, times(1)).setAttribute("merchantId", "merchant_id");
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  public void filterPopulatesMerchantIdRequestAttributeForAuthorizedWarehouseRequests()
      throws ServletException, IOException {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "api_key", null, List.of(new SimpleGrantedAuthority("ROLE_WAREHOUSE"))));

    var warehouse = new Warehouse();
    warehouse.setId("warehouse_id");
    var warehouseClient = new WarehouseClient();
    warehouseClient.setWarehouse(warehouse);
    when(warehouseClientRepository.findByApiKey("api_key"))
        .thenReturn(Optional.of(warehouseClient));

    populator.doFilter(request, response, chain);

    verify(request, times(1)).setAttribute("warehouseId", "warehouse_id");
    verify(chain, times(1)).doFilter(request, response);
  }
}
