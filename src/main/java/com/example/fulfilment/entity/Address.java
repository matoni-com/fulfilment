package com.example.fulfilment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "additional_name")
  private String additionalName;

  @Column(name = "company_name")
  private String companyName;

  @NonNull
  @Column(name = "street", nullable = false)
  private String street;

  @Column(name = "street2")
  private String street2;

  @NonNull
  @Column(name = "house_number", nullable = false)
  private String houseNumber;

  @NonNull
  @Column(name = "zip", nullable = false)
  private String zip;

  @NonNull
  @Column(name = "city", nullable = false)
  private String city;

  @NonNull
  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "postbox")
  private String postbox;

  @Column(name = "gps_location")
  private String gpsLocation;

  @Column(name = "telephone_number")
  private String telephoneNumber;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
