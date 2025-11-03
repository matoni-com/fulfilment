package com.example.fulfilment.repository;

import com.example.fulfilment.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByMerchantId(String merchantId);

  Optional<Product> findByMerchantSkuAndMerchantId(String merchantSku, String merchantId);
}
