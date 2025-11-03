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
public class WarehouseClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "api_key", nullable = false, unique = true)
  private String apiKey;

  @ManyToOne
  @JoinTable(
      name = "warehouses_clients",
      joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id"))
  private Warehouse warehouse;
}
