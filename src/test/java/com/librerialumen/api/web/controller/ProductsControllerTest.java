package com.librerialumen.api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.common.WebMvcTestWithAuth;
import com.librerialumen.api.service.ProductService;
import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTestWithAuth(controllers = ProductsController.class)
class ProductsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProductService productService;

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/products creates a product")
  void createProduct_shouldReturnCreatedProduct() throws Exception {
    UUID categoryId = UUID.randomUUID();
    ProductCreateDTO request = ProductCreateDTO.builder()
        .sku("SKU-100")
        .title("Clean Code")
        .price(BigDecimal.valueOf(45.5))
        .categoryId(categoryId)
        .build();

    ProductViewDTO response = ProductViewDTO.builder()
        .id(UUID.randomUUID())
        .sku("SKU-100")
        .title("Clean Code")
        .price(BigDecimal.valueOf(45.5))
        .categoryId(categoryId)
        .categoryName("Programming")
        .active(true)
        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
        .build();

    when(productService.create(any(ProductCreateDTO.class))).thenReturn(response);

    mockMvc.perform(post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.sku").value("SKU-100"))
        .andExpect(jsonPath("$.categoryId").value(categoryId.toString()));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("POST /api/v1/products returns 400 on invalid payload")
  void createProduct_shouldReturnBadRequest() throws Exception {
    String invalidPayload = "{\"sku\":\"SKU-200\",\"price\":\"not-a-number\"}";

    mockMvc.perform(post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidPayload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));

    verify(productService, never()).create(any(ProductCreateDTO.class));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("PUT /api/v1/products/{id} updates product")
  void updateProduct_shouldReturnUpdatedProduct() throws Exception {
    UUID productId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();
    ProductUpdateDTO request = ProductUpdateDTO.builder()
        .title("Refactoring 2nd Edition")
        .price(BigDecimal.valueOf(58.90))
        .categoryId(categoryId)
        .active(false)
        .build();

    ProductViewDTO response = ProductViewDTO.builder()
        .id(productId)
        .sku("SKU-102")
        .title("Refactoring 2nd Edition")
        .price(BigDecimal.valueOf(58.90))
        .categoryId(categoryId)
        .categoryName("Programming")
        .active(false)
        .updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
        .build();

    when(productService.update(eq(productId), any(ProductUpdateDTO.class))).thenReturn(response);

    mockMvc.perform(put("/api/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId.toString()))
        .andExpect(jsonPath("$.title").value("Refactoring 2nd Edition"))
        .andExpect(jsonPath("$.active").value(false));

    verify(productService).update(eq(productId), any(ProductUpdateDTO.class));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/products returns product list")
  void listProducts_shouldReturnList() throws Exception {
    ProductViewDTO product = ProductViewDTO.builder()
        .id(UUID.randomUUID())
        .sku("SKU-101")
        .title("Domain-Driven Design")
        .price(BigDecimal.valueOf(60))
        .active(true)
        .build();

    when(productService.list()).thenReturn(List.of(product));

    mockMvc.perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sku").value("SKU-101"))
        .andExpect(jsonPath("$[0].title").value("Domain-Driven Design"));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/products/{id} returns product details")
  void getProduct_shouldReturnProduct() throws Exception {
    UUID productId = UUID.randomUUID();
    ProductViewDTO product = ProductViewDTO.builder()
        .id(productId)
        .sku("SKU-102")
        .title("Refactoring")
        .price(BigDecimal.valueOf(55))
        .active(true)
        .build();

    when(productService.get(productId)).thenReturn(product);

    mockMvc.perform(get("/api/v1/products/{id}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId.toString()))
        .andExpect(jsonPath("$.title").value("Refactoring"));
  }
}
