package com.example.fulfilment.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

class ProductControllerTests extends BaseIntegrationSuite {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductRepository productRepository;

  @Autowired private ObjectMapper objectMapper;

  @AfterEach
  void cleanDatabase() {
    productRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void createProduct_shouldPersistProductToDatabase() throws Exception {
    ProductCreateRequest product = new ProductCreateRequest();
    product.setWarehouseId("warehouse1");
    product.setMerchantSku("sku123");
    product.setManufacturerSku("mSku123");
    product.setManufacturerName("Test Manufacturer");
    product.setEan("1234567890123");
    product.setItemName("Test Item");
    product.setIsActive(true);

    mockMvc
        .perform(
            post("/api/merchant/products")
                .requestAttr("merchantId", "merchant1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isOk());

    assertThat(productRepository.findByMerchantSkuAndMerchantId("sku123", "merchant1"))
        .isPresent()
        .get()
        .satisfies(
            savedProduct -> {
              assertThat(savedProduct.getMerchantId()).isEqualTo("merchant1");
              assertThat(savedProduct.getWarehouseId()).isEqualTo("warehouse1");
            });
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void getAllProducts_shouldReturnSavedProducts() throws Exception {
    // given
    Product product = new Product();
    product.setMerchantId("merchant-xyz");
    product.setWarehouseId("warehouse-abc");
    product.setMerchantSku("sku-001");
    product.setItemName("Test Product");
    product.setIsActive(true);

    productRepository.save(product);

    // when + then
    mockMvc
        .perform(get("/api/merchant/products").requestAttr("merchantId", "merchant-xyz"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].merchantId").value("merchant-xyz"))
        .andExpect(jsonPath("$[0].merchantSku").value("sku-001"))
        .andExpect(jsonPath("$[0].itemName").value("Test Product"));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void getProductByMerchantSku_shouldReturnProductResponse() throws Exception {
    // given
    Product product = new Product();
    product.setMerchantId("merchant-123");
    product.setWarehouseId("warehouse-456");
    product.setMerchantSku("sku-789");
    product.setItemName("Sample Product");
    product.setIsActive(true);

    Product savedProduct = productRepository.save(product);

    // when + then
    mockMvc
        .perform(
            get("/api/merchant/products/" + savedProduct.getMerchantSku())
                .requestAttr("merchantId", "merchant-123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.merchantId").value("merchant-123"))
        .andExpect(jsonPath("$.merchantSku").value("sku-789"))
        .andExpect(jsonPath("$.itemName").value("Sample Product"));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void getAllProducts_shouldReturnEmptyListWhenNoProductsExist() throws Exception {
    // when + then
    mockMvc
        .perform(get("/api/merchant/products").requestAttr("merchantId", "merchant-123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void createProduct_shouldReturnBadRequestWhenMandatoryFieldsAreMissing() throws Exception {
    ProductCreateRequest product = new ProductCreateRequest();
    // Leaving all fields empty to trigger validation errors

    mockMvc
        .perform(
            post("/api/merchant/products")
                .requestAttr("merchantId", "merchant-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.warehouseId").value("Warehouse ID is required"))
        .andExpect(jsonPath("$.merchantSku").value("Merchant SKU is required"))
        .andExpect(jsonPath("$.manufacturerSku").value("Manufacturer SKU is required"))
        .andExpect(jsonPath("$.manufacturerName").value("Manufacturer Name is required"))
        .andExpect(jsonPath("$.ean").value("EAN is required"))
        .andExpect(jsonPath("$.itemName").value("Item Name is required"));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void createProduct_shouldReturnBadRequestWhenEanIsInvalid() throws Exception {
    ProductCreateRequest product = new ProductCreateRequest();
    product.setWarehouseId("warehouse1");
    product.setMerchantSku("sku123");
    product.setManufacturerSku("mSku123");
    product.setManufacturerName("Test Manufacturer");
    product.setEan("invalid-ean"); // Invalid EAN
    product.setItemName("Test Item");

    mockMvc
        .perform(
            post("/api/merchant/products")
                .requestAttr("merchantId", "merchant1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.ean").value("EAN must be a 13-digit number"));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void createProduct_shouldReturnBadRequestWhenFieldExceedsMaxLength() throws Exception {
    ProductCreateRequest product = new ProductCreateRequest();
    product.setWarehouseId("warehouse1");
    product.setMerchantSku("a".repeat(51)); // Exceeds max length of 50
    product.setManufacturerSku("mSku123");
    product.setManufacturerName("Test Manufacturer");
    product.setEan("1234567890123");
    product.setItemName("Test Item");

    mockMvc
        .perform(
            post("/api/merchant/products")
                .requestAttr("merchantId", "merchant1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.merchantSku").value("Merchant SKU must not exceed 50 characters"));
  }

  @Test
  @WithMockUser(roles = {"MERCHANT"})
  void deactivateProduct_shouldSetIsActiveToFalse() throws Exception {
    // given
    Product product = new Product();
    product.setMerchantId("merchant-123");
    product.setWarehouseId("warehouse-456");
    product.setMerchantSku("sku-789");
    product.setItemName("Sample Product");
    product.setIsActive(true);

    Product savedProduct = productRepository.save(product);

    // when
    mockMvc
        .perform(
            patch("/api/merchant/products/" + savedProduct.getMerchantSku() + "/deactivate")
                .requestAttr("merchantId", "merchant-123"))
        .andExpect(status().isNoContent());

    // then
    Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
    assertThat(updatedProduct.getIsActive()).isFalse();
  }
}
