package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.ProductService;
import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Products", description = "Product catalog operations")
@SecurityRequirement(name = "bearerAuth")
public class ProductsController {

  private final ProductService productService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create product", description = "Registers a new product in the catalog")
  public ProductViewDTO create(@Valid @RequestBody ProductCreateDTO dto) {
    return productService.create(dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update product", description = "Updates product details")
  public ProductViewDTO update(
      @Parameter(description = "Product identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody ProductUpdateDTO dto) {
    return productService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Patch product", description = "Applies a partial update to product details")
  public ProductViewDTO patch(
      @Parameter(description = "Product identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody ProductUpdateDTO dto) {
    return productService.patch(id, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete product", description = "Removes a product by identifier")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Product identifier") @PathVariable("id") UUID id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN')")
  @Operation(summary = "List products", description = "Returns all products available")
  public List<ProductViewDTO> list() {
    return productService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  @Operation(summary = "Get product", description = "Fetches a product by identifier")
  public ProductViewDTO get(
      @Parameter(description = "Product identifier") @PathVariable("id") UUID id) {
    return productService.get(id);
  }
}
