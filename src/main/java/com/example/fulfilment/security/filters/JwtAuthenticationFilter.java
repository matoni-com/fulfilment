package com.example.fulfilment.security.filters;

import com.example.fulfilment.security.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/*Filters should not be beans, because in that case they are automatically started as a servlet filter by Spring
in addition to being explicitly started as a part of the security filter chain. This would lead to issues like the
filter being executed for endpoints that it was not explicitly registered for or being executed twice for endpoints
that it was registered for.*/
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private AuthenticationManager authManager;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      /* This means the http request is just passed on to the built-in AuthorizationFilter.
      We need to have a call to filterChain.doFilter and not just throw an exception, otherwise
      an http request without a token to endpoints marked as .permitAll() would be denied because
      an exception would be thrown and the request would never reach the built-in AuthorizationFilter.*/
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.replace("Bearer ", "");

    JwtAuthenticationToken auth = new JwtAuthenticationToken(token);

    Authentication authenticated = authManager.authenticate(auth);

    SecurityContextHolder.getContext().setAuthentication(authenticated);

    filterChain.doFilter(request, response);
  }
}
