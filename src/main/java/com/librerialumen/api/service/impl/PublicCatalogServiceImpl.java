package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.CategoryRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.service.InventoryService;
import com.librerialumen.api.service.PublicCatalogService;
import com.librerialumen.api.web.dto.catalog.PublicCategoryViewDTO;
import com.librerialumen.api.web.dto.catalog.PublicProductViewDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCatalogServiceImpl implements PublicCatalogService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final InventoryService inventoryService;

  @Override
  public List<PublicProductViewDTO> listProducts() {
    return productRepository.findByActiveTrueOrderByTitleAsc().stream()
        .map(this::toProductView)
        .toList();
  }

  @Override
  public PublicProductViewDTO getProduct(UUID productId) {
    Product product = productRepository.findByIdAndActiveTrue(productId)
        .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));
    return toProductView(product);
  }

  @Override
  public List<PublicCategoryViewDTO> listCategories() {
    return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
        .map(category -> PublicCategoryViewDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .build())
        .toList();
  }

  @Override
  public PublicCategoryViewDTO getCategory(UUID categoryId) {
    Category category = categoryRepository.findByIdAndActiveTrue(categoryId)
        .orElseThrow(() -> new BusinessException("CATEGORY_NOT_FOUND", "Category not found"));
    return PublicCategoryViewDTO.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .build();
  }

  private PublicProductViewDTO toProductView(Product product) {
    int stock = inventoryService.getCurrentStock(product.getId());
    return PublicProductViewDTO.builder()
        .id(product.getId())
        .title(product.getTitle())
        .author(product.getAuthor())
        .price(product.getPrice())
        .categoryId(product.getCategory().getId())
        .categoryName(product.getCategory().getName())
        .stock(stock)
        .build();
  }
}
