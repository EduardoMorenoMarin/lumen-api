package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.ProductMapper;
import com.librerialumen.api.repository.CategoryRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.ProductService;
import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;
  private final AuditService auditService;

  @Override
  public ProductViewDTO create(ProductCreateDTO dto) {
    Category category = resolveCategory(dto.getCategoryId());

    Product product = productMapper.toEntity(dto);
    product.setCategory(category);
    product.setActive(dto.getActive() != null ? dto.getActive() : true);

    Product saved = productRepository.save(product);
    auditProduct(saved, "CREATE");
    return productMapper.toView(saved);
  }

  @Override
  public ProductViewDTO update(UUID productId, ProductUpdateDTO dto) {
    return applyUpdate(productId, dto, "UPDATE");
  }

  @Override
  public ProductViewDTO patch(UUID productId, ProductUpdateDTO dto) {
    return applyUpdate(productId, dto, "PATCH");
  }

  @Override
  @Transactional(readOnly = true)
  public ProductViewDTO get(UUID productId) {
    return productMapper.toView(loadProduct(productId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ProductViewDTO> list() {
    return productMapper.toViewList(productRepository.findAll());
  }

  @Override
  public void delete(UUID productId) {
    Product product = loadProduct(productId);
    try {
      productRepository.delete(product);
      productRepository.flush();
    } catch (DataIntegrityViolationException ex) {
      throw new BusinessException("PRODUCT_DELETE_CONSTRAINT",
          "Unable to delete product because it is referenced by other records.", ex);
    }
    auditProduct(product, "DELETE");
  }

  private ProductViewDTO applyUpdate(UUID productId, ProductUpdateDTO dto, String action) {
    Product product = loadProduct(productId);

    if (dto.getCategoryId() != null) {
      Category category = resolveCategory(dto.getCategoryId());
      product.setCategory(category);
    }

    if (dto.getActive() != null) {
      product.setActive(dto.getActive());
    }

    productMapper.updateEntity(dto, product);
    Product updated = productRepository.save(product);
    Map<String, Object> details = buildAuditDetails(updated);
    details.put("updatedFields", dto);
    auditProduct(updated, action, details);
    return productMapper.toView(updated);
  }

  private Product loadProduct(UUID productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));
  }

  private Category resolveCategory(UUID categoryId) {
    if (categoryId == null) {
      throw new BusinessException("CATEGORY_REQUIRED", "Category is required for products");
    }
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new BusinessException("CATEGORY_NOT_FOUND", "Category not found"));
  }

  private void auditProduct(Product product, String action) {
    auditProduct(product, action, buildAuditDetails(product));
  }

  private void auditProduct(Product product, String action, Map<String, Object> details) {
    auditService.record("Product", product.getId().toString(), action, null, details);
  }

  private Map<String, Object> buildAuditDetails(Product product) {
    Map<String, Object> details = new HashMap<>();
    if (product.getSku() != null) {
      details.put("sku", product.getSku());
    }
    if (product.getTitle() != null) {
      details.put("title", product.getTitle());
    }
    details.put("active", product.isActive());
    if (product.getCategory() != null) {
      details.put("categoryId", product.getCategory().getId());
    }
    return details;
  }
}

