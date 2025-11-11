package com.example.fulfilment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the connection between a merchant integration configuration and a warehouse
 * integration configuration. This allows merchants to connect their integration configurations with
 * specific warehouse integration configurations.
 */
@Entity
@Table(name = "merchant_warehouse_integration_connections")
@Getter
@Setter
@NoArgsConstructor
public class MerchantWarehouseIntegrationConnection {
  @Id
  @Column(name = "id")
  private String id;

  @ManyToOne
  @JoinColumn(name = "merchant_integration_configuration_id", nullable = false)
  private MerchantIntegrationConfiguration merchantIntegrationConfiguration;

  @ManyToOne
  @JoinColumn(name = "warehouse_integration_configuration_id", nullable = false)
  private WarehouseIntegrationConfiguration warehouseIntegrationConfiguration;
}

