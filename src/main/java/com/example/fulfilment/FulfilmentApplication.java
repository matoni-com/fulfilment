package com.example.fulfilment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FulfilmentApplication {
  public static void main(String[] args) {
    SpringApplication.run(FulfilmentApplication.class, args);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @PreAuthorize("hasAuthority('HELLO2')")
  @GetMapping("/hello2")
  public String hello2(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @PreAuthorize("hasAuthority('HELLO2') and @authExpressions.hasAccessToMerchant(#merchantId)")
  @GetMapping("/hello2/{merchantId}")
  public String hello2WithMerchantId(@PathVariable String merchantId) {
    return String.format("Hello %s!", merchantId);
  }
}
