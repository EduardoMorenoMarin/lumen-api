package com.librerialumen.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librerialumen.api.common.CategoryTestBuilder;
import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.ProductMapper;
import com.librerialumen.api.repository.CategoryRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private ProductMapper productMapper;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private ProductServiceImpl productService;

  @Test
  void create_shouldDefaultActiveAndAudit() {
    UUID categoryId = UUID.randomUUID();
    ProductCreateDTO dto = ProductCreateDTO.builder()
        .categoryId(categoryId)
        .sku("SKU-1")
        .title("Title")
        .build();

    Category category = CategoryTestBuilder.aCategory().withId(categoryId).build();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    Product productEntity = new Product();
    productEntity.setActive(false);
    when(productMapper.toEntity(dto)).thenReturn(productEntity);
    when(productRepository.save(productEntity)).thenAnswer(invocation -> {
      Product product = invocation.getArgument(0);
      product.setId(UUID.randomUUID());
      return product;
    });
    when(productMapper.toView(any(Product.class))).thenReturn(new ProductViewDTO());

    productService.create(dto);

    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productCaptor.capture());
    Product saved = productCaptor.getValue();
    assertTrue(saved.isActive());
    assertEquals(category, saved.getCategory());
    verify(auditService).record(eq("Product"), anyString(), eq("CREATE"), any(), any());
  }

  @Test
  void create_shouldFailWhenCategoryMissing() {
    ProductCreateDTO dto = ProductCreateDTO.builder()
        .title("No category")
        .build();

    BusinessException ex = assertThrows(BusinessException.class, () -> productService.create(dto));
    assertEquals("CATEGORY_REQUIRED", ex.getCode());
  }

  @Test
  void update_shouldResolveCategoryWhenProvided() {
    UUID productId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();
    ProductUpdateDTO dto = ProductUpdateDTO.builder()
        .categoryId(categoryId)
        .active(false)
        .build();

    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    Category category = CategoryTestBuilder.aCategory().withId(categoryId).build();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(productRepository.save(product)).thenReturn(product);
    when(productMapper.toView(product)).thenReturn(new ProductViewDTO());

    productService.update(productId, dto);

    verify(productMapper).updateEntity(dto, product);
    assertEquals(category, product.getCategory());
    assertEquals(false, product.isActive());
    verify(auditService).record(eq("Product"), anyString(), eq("UPDATE"), any(), any());
  }

  @Test
  void delete_shouldTranslateConstraintViolation() {
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    doThrow(new DataIntegrityViolationException("constraint")).when(productRepository).delete(product);

    BusinessException ex = assertThrows(BusinessException.class, () -> productService.delete(productId));
    assertEquals("PRODUCT_DELETE_CONSTRAINT", ex.getCode());
  }
}

