package com.librerialumen.api.repository;

import com.librerialumen.api.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  List<Product> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSkuContainingIgnoreCase(
      String title,
      String author,
      String sku);

  List<Product> findByCategoryId(UUID categoryId);

  List<Product> findByActiveTrueOrderByTitleAsc();

  Optional<Product> findByIdAndActiveTrue(UUID productId);
}
