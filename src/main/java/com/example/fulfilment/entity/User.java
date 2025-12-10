package com.example.fulfilment.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NonNull
  @Column(name = "username", nullable = false)
  private String username;

  @NonNull
  @Column(name = "password", nullable = false)
  private String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "users_authorities", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "authority")
  @Enumerated(EnumType.STRING)
  private Set<UserAuthority> authorities = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_merchants",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "merchant_id"))
  private Set<Merchant> merchants = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_warehouses",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "warehouse_id"))
  private Set<Warehouse> warehouses = new HashSet<>();
}
