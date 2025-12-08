package com.example.fulfilment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "api_key", nullable = false, unique = true)
  private String apiKey;

  @Column(name = "api_secret", nullable = false)
  private String apiSecret;

  @Column(name = "role", nullable = false)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private ClientRole role;
}
