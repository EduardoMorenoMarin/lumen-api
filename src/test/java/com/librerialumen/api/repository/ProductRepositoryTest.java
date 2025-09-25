package com.librerialumen.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.librerialumen.api.ActiveTestProfile;
import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@ActiveTestProfile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSkuContainingIgnoreCase matches different fields")
  void searchByTitleAuthorOrSku_shouldMatchAllFields() {
    Category category = persistCategory("Literature");

    Product titleMatch = persistProduct("SKU-DOMAIN", "Domain-Driven Design", "Eric Evans", category);
    Product authorMatch = persistProduct("SKU-REFACTOR", "Refactoring", "Martin Fowler", category);
    Product skuMatch = persistProduct("SKU-SEARCH", "Clean Architecture", "Robert C. Martin", category);

    List<Product> byTitle = productRepository
        .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSkuContainingIgnoreCase("domain", "domain", "domain");
    assertEquals(1, byTitle.size());
    assertEquals(titleMatch.getId(), byTitle.get(0).getId());

    List<Product> byAuthor = productRepository
        .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSkuContainingIgnoreCase("martin", "martin", "martin");
    Set<String> authorResultIds = byAuthor.stream().map(product -> product.getId().toString()).collect(Collectors.toSet());
    assertEquals(2, authorResultIds.size());
    assertTrue(authorResultIds.containsAll(Set.of(authorMatch.getId().toString(), skuMatch.getId().toString())));

    List<Product> bySku = productRepository
        .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSkuContainingIgnoreCase("sku-search", "sku-search", "sku-search");
    assertEquals(1, bySku.size());
    assertEquals(skuMatch.getId(), bySku.get(0).getId());
  }

  @Test
  @DisplayName("findByIdAndActiveTrue returns product only when active")
  void findByIdAndActiveTrue_shouldFilterInactive() {
    Category category = persistCategory("Technology");

    Product active = persistProduct("SKU-ACTIVE", "Effective Java", "Joshua Bloch", category);
    Product inactive = persistInactiveProduct("SKU-INACTIVE", "Old Book", "Anonymous", category);

    Optional<Product> foundActive = productRepository.findByIdAndActiveTrue(active.getId());
    Optional<Product> foundInactive = productRepository.findByIdAndActiveTrue(inactive.getId());

    assertTrue(foundActive.isPresent());
    assertTrue(foundInactive.isEmpty());
  }

  private Category persistCategory(String name) {
    Category category = new Category();
    category.setName(name);
    category.setDescription(name + " category");
    category.setActive(true);
    entityManager.persist(category);
    return category;
  }

  private Product persistProduct(String sku, String title, String author, Category category) {
    Product product = new Product();
    product.setSku(sku);
    product.setIsbn(sku + "-ISBN");
    product.setTitle(title);
    product.setAuthor(author);
    product.setDescription(title + " description");
    product.setPrice(new BigDecimal("45.00"));
    product.setActive(true);
    product.setCategory(category);
    entityManager.persist(product);
    return product;
  }

  private Product persistInactiveProduct(String sku, String title, String author, Category category) {
    Product product = persistProduct(sku, title, author, category);
    product.setActive(false);
    entityManager.flush();
    return product;
  }
}
