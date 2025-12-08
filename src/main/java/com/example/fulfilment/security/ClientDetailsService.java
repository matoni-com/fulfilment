package com.example.fulfilment.security;

import com.example.fulfilment.entity.Client;
import com.example.fulfilment.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClientDetailsService implements UserDetailsService {

  private ClientRepository clientRepository;

  @Override
  // In this use case username is apiKey
  public UserDetails loadUserByUsername(String apiKey) throws UsernameNotFoundException {
    Client client =
        clientRepository
            .findByApiKey(apiKey)
            .orElseThrow(
                () -> new UsernameNotFoundException("Client not found with apiKey: " + apiKey));

    UserDetails userDetails =
        org.springframework.security.core.userdetails.User.builder()
            .username(client.getApiKey())
            .password(client.getApiSecret())
            .roles(client.getRole().name())
            .build();

    return userDetails;
  }
}
