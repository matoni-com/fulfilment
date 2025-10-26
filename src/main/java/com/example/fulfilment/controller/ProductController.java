package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.service.ProductService;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/products")
public class ProductController {

  private final ProductService productService;
  private final ProductControllerMapper productControllerMapper;

  public ProductController(
      ProductService productService, ProductControllerMapper productControllerMapper) {
    this.productService = productService;
    this.productControllerMapper = productControllerMapper;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @RequestAttribute("merchantId") String merchantId,
      @Valid @RequestBody ProductCreateRequest request) {
    ProductCreateCommand productCreateCommand =
        productControllerMapper.toCommand(request, merchantId);
    ProductResult savedProduct = productService.saveProduct(productCreateCommand);
    ProductResponse productResponse = productControllerMapper.fromProductResult(savedProduct);
    return ResponseEntity.ok(productResponse);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts(
      @RequestAttribute("merchantId") String merchantId) {
    List<ProductResult> products = productService.getProductsByMerchantId(merchantId);
    List<ProductResponse> productResponses =
        products.stream().map(productControllerMapper::fromProductResult).toList();
    return ResponseEntity.ok(productResponses);
  }

  @GetMapping("/{merchantSku}")
  public ResponseEntity<ProductResponse> getProductByMerchantSku(
      @PathVariable String merchantSku, @RequestAttribute("merchantId") String merchantId) {
    return productService
        .getProductByMerchantSkuAndMerchantId(merchantSku, merchantId)
        .map(productControllerMapper::fromProductResult)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{merchantSku}/deactivate")
  public ResponseEntity<Void> deactivateProduct(
      @PathVariable String merchantSku, @RequestAttribute("merchantId") String merchantId) {
    productService.deactivateProduct(merchantSku, merchantId);
    return ResponseEntity.noContent().build();
  }
}
