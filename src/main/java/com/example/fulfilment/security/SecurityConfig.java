package com.example.fulfilment.security;

import com.example.fulfilment.repository.MerchantClientRepository;
import com.example.fulfilment.repository.WarehouseClientRepository;
import com.example.fulfilment.security.exceptionhandling.RestAccessDeniedHandler;
import com.example.fulfilment.security.exceptionhandling.RestAuthenticationEntryPoint;
import com.example.fulfilment.security.filters.JwtAuthenticationFilter;
import com.example.fulfilment.security.filters.MerchantAndWarehouseRequestAttributePopulator;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecretKey jwtSignatureKey() {
    return Jwts.SIG.HS256.key().build();
  }

  @Bean(name = "webUserUsernamePassword")
  public AuthenticationManager webUserUsernamePasswordAuthManager(
      BCryptPasswordEncoder passwordEncoder, WebUserDetailsService webUserDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(webUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(provider);
  }

  @Bean
  public SecurityFilterChain webUserApiSecurityFilterChain(
      HttpSecurity http, JwtAuthenticationProvider provider) throws Exception {

    AuthenticationManager jwtAuthManager = new ProviderManager(provider);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtAuthManager);

    return http.securityMatcher("/web/**", "/authenticate", "/hello**")
        /* This will put the JwtAuthenticationFilter after the ExceptionTranslationFilter in the filter chain.
        This way ExceptionTranslationFilter can handle exceptions that are thrown in JwtAuthenticationFilter
        since they will bubble up through the call stack. ExceptionTranslationFilter handles the exceptions
        using the AuthenticationEntryPoint and AccessDeniedHandler that we configured below.*/
        .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class)
        .exceptionHandling(
            eh ->
                eh.authenticationEntryPoint(
                        new RestAuthenticationEntryPoint("Invalid or missing token"))
                    .accessDeniedHandler(new RestAccessDeniedHandler("Access denied")))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .build();
  }

  @Bean
  public SecurityFilterChain clientApiSecurityFilterChain(
      HttpSecurity http,
      ClientDetailsService clientDetailsService,
      BCryptPasswordEncoder passwordEncoder,
      MerchantClientRepository merchantClientRepository,
      WarehouseClientRepository warehouseClientRepository)
      throws Exception {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(clientDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    AuthenticationManager clientApiAuthManager = new ProviderManager(provider);

    MerchantAndWarehouseRequestAttributePopulator attributePopulator =
        new MerchantAndWarehouseRequestAttributePopulator(
            merchantClientRepository, warehouseClientRepository);

    RestAuthenticationEntryPoint authenticationEntryPoint =
        new RestAuthenticationEntryPoint("Invalid or missing API key/secret");

    return http.securityMatcher("/api/**")
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/merchant/**")
                    .hasRole("MERCHANT")
                    .requestMatchers("/api/warehouse/**")
                    .hasRole("WAREHOUSE")
                    .anyRequest()
                    .denyAll())
        .authenticationManager(clientApiAuthManager)
        /* BasicAuthenticationFilter handles authentication failures internally using its own AuthenticationEntryPoint,
        rather than delegating to the global exception handling configured below. In other words it does not bubble up
        exceptions through the call stack. Therefore, we must explicitly set the same custom entry point here to ensure
        consistent error responses. In case of invalid creds it throw an AuthenticationException itself, and in case
        Basic header is missing it delegates to the AuthorizationFilter that can then throw an AuthenticationException
        if the endpoint requires authentication. This behaviour is similar to our custom JwtAuthenticationFilter.*/
        .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterAfter(attributePopulator, BasicAuthenticationFilter.class)
        .exceptionHandling(
            eh ->
                eh.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(new RestAccessDeniedHandler("Access denied")))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .build();
  }
}
