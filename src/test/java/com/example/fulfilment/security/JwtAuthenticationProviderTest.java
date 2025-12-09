package com.example.fulfilment.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.fulfilment.security.jwt.InvalidJwtException;
import com.example.fulfilment.security.jwt.JwtValidationResult;
import com.example.fulfilment.security.jwt.JwtValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationProviderTest {

  @Mock private JwtValidator jwtValidator;

  @InjectMocks private JwtAuthenticationProvider provider;

  @Test
  public void successfulAuthenticationWithMerchantAndWarehouseIds() throws InvalidJwtException {
    String jwt = "valid_jwt_token";
    JwtAuthenticationToken unauthenticatedToken = new JwtAuthenticationToken(jwt);

    JwtValidationResult validationResult =
        new JwtValidationResult(
            "testuser",
            List.of("AUTHORITY1", "AUTHORITY2"),
            List.of("M1", "M2"),
            List.of("W1", "W2"));

    when(jwtValidator.validateToken(jwt)).thenReturn(validationResult);

    Authentication result = provider.authenticate(unauthenticatedToken);

    assertThat(result).isInstanceOf(JwtAuthenticationToken.class);
    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) result;

    assertThat(jwtAuth.getPrincipal()).isEqualTo("testuser");
    assertThat(jwtAuth.getCredentials()).isEqualTo(jwt);
    assertThat(jwtAuth.getAuthorities())
        .containsExactly(
            new SimpleGrantedAuthority("AUTHORITY1"), new SimpleGrantedAuthority("AUTHORITY2"));
    assertThat(jwtAuth.getMerchantIds()).containsExactly("M1", "M2");
    assertThat(jwtAuth.getWarehouseIds()).containsExactly("W1", "W2");
    assertThat(jwtAuth.isAuthenticated()).isTrue();

    verify(jwtValidator, times(1)).validateToken(jwt);
  }

  @Test
  public void successfulAuthenticationWithEmptyMerchantAndWarehouseIds()
      throws InvalidJwtException {
    String jwt = "valid_jwt_token";
    JwtAuthenticationToken unauthenticatedToken = new JwtAuthenticationToken(jwt);

    JwtValidationResult validationResult =
        new JwtValidationResult("testuser", List.of("ROLE_USER"), List.of(), List.of());

    when(jwtValidator.validateToken(jwt)).thenReturn(validationResult);

    Authentication result = provider.authenticate(unauthenticatedToken);

    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) result;

    assertThat(jwtAuth.getMerchantIds()).isEmpty();
    assertThat(jwtAuth.getWarehouseIds()).isEmpty();
  }

  @Test
  public void invalidJwtThrowsBadCredentialsException() throws InvalidJwtException {
    String jwt = "invalid_jwt_token";
    JwtAuthenticationToken unauthenticatedToken = new JwtAuthenticationToken(jwt);

    when(jwtValidator.validateToken(jwt))
        .thenThrow(new InvalidJwtException("Invalid JWT", new RuntimeException()));

    assertThatThrownBy(() -> provider.authenticate(unauthenticatedToken))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Invalid JWT");

    verify(jwtValidator, times(1)).validateToken(jwt);
  }

  @Test
  public void supportsJwtAuthenticationToken() {
    assertThat(provider.supports(JwtAuthenticationToken.class)).isTrue();
  }

  @Test
  public void doesNotSupportOtherAuthenticationTypes() {
    assertThat(provider.supports(Authentication.class)).isFalse();
  }
}
