package com.example.fulfilment.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sealed base class for connection settings. Similar to sealed trait in Scala. Each connection type has
 * its own specific fields. Using sealed class allows exhaustive pattern matching in switch expressions
 * (Java 21+).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ApiKeyConnection.class, name = "API_KEY"),
  @JsonSubTypes.Type(value = UsernamePasswordConnection.class, name = "USERNAME_PASSWORD"),
  @JsonSubTypes.Type(value = FtpConnection.class, name = "FTP")
})
@Getter
@Setter
@NoArgsConstructor
public sealed class ConnectionSettings
    permits ApiKeyConnection, UsernamePasswordConnection, FtpConnection {
  // Base class for all connection types
}

