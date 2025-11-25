package com.example.fulfilment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "merchant_flows")
@Getter
@Setter
@NoArgsConstructor
public class MerchantFlow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "merchant_integration_configuration_id", nullable = false)
  private MerchantIntegrationConfiguration merchantIntegrationConfiguration;

  @Enumerated(EnumType.STRING)
  @Column(name = "flow_kind", nullable = false)
  private FlowKind flowKind;

  @Enumerated(EnumType.STRING)
  @Column(name = "direction", nullable = false)
  private FlowDirection direction;

  @Enumerated(EnumType.STRING)
  @Column(name = "execution_mode", nullable = false)
  private ExecutionMode executionMode;

  // Cron expression or ISO-8601 duration (e.g. PT5M) for polling; nullable for PASSIVE flows
  @Column(name = "schedule")
  private String schedule;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "last_run_at")
  private LocalDateTime lastRunAt;

  @Column(name = "next_planned_run_at")
  private LocalDateTime nextPlannedRunAt;

  @Column(name = "notes")
  private String notes;
}
