package com.example.fulfilment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class MerchantClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "api_key", nullable = false, unique = true)
  private String apiKey;

  @ManyToOne
  @JoinTable(
      name = "merchants_clients",
      joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "merchant_id", referencedColumnName = "id"))
  private Merchant merchant;
}
