package com.example.fulfilment.service;

import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductServiceMapper productServiceMapper;

  public ProductService(
      ProductRepository productRepository, ProductServiceMapper productServiceMapper) {
    this.productRepository = productRepository;
    this.productServiceMapper = productServiceMapper;
  }

  public ProductResult saveProduct(ProductCreateCommand command) {
    Product product = productServiceMapper.toEntity(command);
    Product savedProduct = productRepository.save(product);
    return productServiceMapper.toResult(savedProduct);
  }

  public List<ProductResult> getProductsByMerchantId(String merchantId) {
    return productRepository.findByMerchantCodeptId(merchantId).stream()
        .map(productServiceMapper::toResult)
        .collect(Collectors.toList());
  }

  public Optional<ProductResult> getProductByMerchantSkuAndMerchantId(
      String merchantSku, String merchantId) {
    return productRepository
        .findByMerchantSkuAndMerchantCodeptId(merchantSku, merchantId)
        .map(productServiceMapper::toResult);
  }

  public void deactivateProduct(String merchantSku, String merchantId) {
    Product product =
        productRepository
            .findByMerchantSkuAndMerchantCodeptId(merchantSku, merchantId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Product not found with merchantSku: "
                            + merchantSku
                            + " and merchantId: "
                            + merchantId));
    product.setIsActive(false);
    productRepository.save(product);
  }
}
