package com.example.fulfilment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "warehouse_name")
  private String warehouseName;

  @OneToOne
  @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
  private Address address;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
