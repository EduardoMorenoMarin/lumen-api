package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.PublicCatalogService;
import com.librerialumen.api.web.dto.catalog.PublicCategoryViewDTO;
import com.librerialumen.api.web.dto.catalog.PublicProductViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "Public Catalog", description = "Public access to product catalog")
public class PublicCatalogController {

  private final PublicCatalogService publicCatalogService;

  @GetMapping("/products")
  @Operation(summary = "List products", description = "Lists active products with current stock")
  public List<PublicProductViewDTO> listProducts() {
    return publicCatalogService.listProducts();
  }

  @GetMapping("/products/{id}")
  @Operation(summary = "Get product", description = "Retrieves a single active product with stock")
  public PublicProductViewDTO getProduct(@PathVariable("id") UUID id) {
    return publicCatalogService.getProduct(id);
  }

  @GetMapping("/categories")
  @Operation(summary = "List categories", description = "Lists active product categories")
  public List<PublicCategoryViewDTO> listCategories() {
    return publicCatalogService.listCategories();
  }

  @GetMapping("/categories/{id}")
  @Operation(summary = "Get category", description = "Retrieves a single active category")
  public PublicCategoryViewDTO getCategory(@PathVariable("id") UUID id) {
    return publicCatalogService.getCategory(id);
  }
}
