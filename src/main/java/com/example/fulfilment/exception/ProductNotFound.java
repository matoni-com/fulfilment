package com.example.fulfilment.exception;

public class ProductNotFound extends EntityNotFound {
  public ProductNotFound(String merchantSku, String merchantId) {
    super("Product not found with merchantSku: " + merchantSku + " and merchantId: " + merchantId);
  }
}
