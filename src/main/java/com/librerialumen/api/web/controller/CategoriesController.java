package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.CategoryService;
import com.librerialumen.api.web.dto.category.CategoryCreateDTO;
import com.librerialumen.api.web.dto.category.CategoryUpdateDTO;
import com.librerialumen.api.web.dto.category.CategoryViewDTO;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories", description = "Operations for managing product categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoriesController {

  private final CategoryService categoryService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create category", description = "Registers a new product category")
  public CategoryViewDTO create(@Valid @RequestBody CategoryCreateDTO dto) {
    return categoryService.create(dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update category", description = "Updates an existing product category")
  public CategoryViewDTO update(
      @Parameter(description = "Category identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody CategoryUpdateDTO dto) {
    return categoryService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Patch category", description = "Applies a partial update to a product category")
  public CategoryViewDTO patch(
      @Parameter(description = "Category identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody CategoryUpdateDTO dto) {
    return categoryService.patch(id, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete category", description = "Removes a category by identifier")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Category identifier") @PathVariable("id") UUID id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
  @Operation(summary = "List categories", description = "Returns all categories available")
  public List<CategoryViewDTO> list() {
    return categoryService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
  @Operation(summary = "Get category", description = "Fetches a category by its identifier")
  public CategoryViewDTO get(
      @Parameter(description = "Category identifier") @PathVariable("id") UUID id) {
    return categoryService.get(id);
  }
}
