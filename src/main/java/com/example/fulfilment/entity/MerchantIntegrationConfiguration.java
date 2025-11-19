package com.example.fulfilment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity for storing merchant integration connection configurations. Uses a sealed-trait-like
 * pattern similar to Scala sealed traits, where ConnectionSettings is the base class and specific
 * implementations (ApiKeyConnection, UsernamePasswordConnection, FtpConnection) are stored as JSONB
 * in the database.
 */
@Entity
@Table(name = "merchant_integration_configurations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantIntegrationConfiguration {
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "merchant_id", nullable = false)
  private Merchant merchant;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "connection_settings", nullable = false, columnDefinition = "jsonb")
  private ConnectionSettings connectionSettings;

  /** Sets the connection settings to ApiKeyConnection. */
  public void setApiKeyConnection(String apiSecret, String url) {
    this.connectionSettings = new ApiKeyConnection(apiSecret, url);
  }

  /** Sets the connection settings to UsernamePasswordConnection. */
  public void setUsernamePasswordConnection(String username, String password, String url) {
    this.connectionSettings = new UsernamePasswordConnection(username, password, url);
  }

  /** Sets the connection settings to FtpConnection. */
  public void setFtpConnection(String host, String username, String password, Integer port) {
    this.connectionSettings = new FtpConnection(host, username, password, port);
  }
}
