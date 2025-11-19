package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.exception.ProductNotFound;
import com.example.fulfilment.service.ProductService;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
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
  @ResponseStatus(HttpStatus.OK)
  public ProductResponse createProduct(
      @RequestAttribute("merchantId") String merchantId,
      @Valid @RequestBody ProductCreateRequest request) {
    ProductCreateCommand productCreateCommand =
        productControllerMapper.toCommand(request, merchantId);
    ProductResult savedProduct = productService.saveProduct(productCreateCommand);
    return productControllerMapper.fromProductResult(savedProduct);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ProductResponse> getAllProducts(@RequestAttribute("merchantId") String merchantId) {
    List<ProductResult> products = productService.getProductsByMerchantId(merchantId);
    return products.stream().map(productControllerMapper::fromProductResult).toList();
  }

  @GetMapping("/{merchantSku}")
  @ResponseStatus(HttpStatus.OK)
  public ProductResponse getProductByMerchantSku(
      @PathVariable String merchantSku, @RequestAttribute("merchantId") String merchantId)
      throws ProductNotFound {
    return productService
        .getProductByMerchantSkuAndMerchantId(merchantSku, merchantId)
        .map(productControllerMapper::fromProductResult)
        .orElseThrow(() -> new ProductNotFound(merchantSku, merchantId));
  }

  @PatchMapping("/{merchantSku}/deactivate")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deactivateProduct(
      @PathVariable String merchantSku, @RequestAttribute("merchantId") String merchantId)
      throws ProductNotFound {
    productService.deactivateProduct(merchantSku, merchantId);
  }
}
