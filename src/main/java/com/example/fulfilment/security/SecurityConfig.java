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

  @Bean(name = "maggieUsernamePassword")
  public AuthenticationManager maggieUsernamePasswordAuthManager(
      BCryptPasswordEncoder passwordEncoder, MaggieUserDetailsService maggieUserDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(maggieUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(provider);
  }

  @Bean
  public SecurityFilterChain maggieSecurityFilterChain(
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
        .httpBasic(httpBasic -> {})
        .addFilterAfter(attributePopulator, BasicAuthenticationFilter.class)
        .exceptionHandling(
            eh ->
                eh.authenticationEntryPoint(
                        new RestAuthenticationEntryPoint("Invalid or missing API key/secret"))
                    .accessDeniedHandler(new RestAccessDeniedHandler("Access denied")))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .build();
  }
}
