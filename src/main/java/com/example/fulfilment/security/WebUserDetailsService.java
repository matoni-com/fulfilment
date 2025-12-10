package com.example.fulfilment.security;

import com.example.fulfilment.entity.Merchant;
import com.example.fulfilment.entity.User;
import com.example.fulfilment.entity.Warehouse;
import com.example.fulfilment.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WebUserDetailsService implements UserDetailsService {

  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found!"));

    // Extract merchant and warehouse IDs
    List<String> merchantIds = user.getMerchants().stream().map(Merchant::getId).toList();

    List<String> warehouseIds = user.getWarehouses().stream().map(Warehouse::getId).toList();

    // Create authorities
    List<SimpleGrantedAuthority> authorities =
        user.getAuthorities().stream()
            .map(auth -> new SimpleGrantedAuthority(auth.name()))
            .toList();

    return new WebUserDetails(
        user.getUsername(), user.getPassword(), authorities, merchantIds, warehouseIds);
  }
}
