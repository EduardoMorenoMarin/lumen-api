package com.librerialumen.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.librerialumen.api.ActiveTestProfile;
import com.librerialumen.api.domain.enums.InventoryMovementType;
import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.InventoryMovement;
import com.librerialumen.api.domain.model.Product;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@ActiveTestProfile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryMovementRepositoryTest {

  @Autowired
  private InventoryMovementRepository inventoryMovementRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("calculateCurrentStock adds IN movements and subtracts OUT movements")
  void calculateCurrentStock_shouldReturnNetQuantity() {
    Category category = persistCategory("Books");
    Product product = persistProduct("SKU-INV-1", "Inventory product", category);
    Product otherProduct = persistProduct("SKU-INV-2", "Other product", category);

    persistMovement(product, InventoryMovementType.IN, 5);
    persistMovement(product, InventoryMovementType.OUT, 2);
    persistMovement(product, InventoryMovementType.ADJUSTMENT, 10);
    persistMovement(otherProduct, InventoryMovementType.IN, 20);

    Integer stock = inventoryMovementRepository.calculateCurrentStock(product.getId());

    assertEquals(3, stock);
  }

  private Category persistCategory(String name) {
    Category category = new Category();
    category.setName(name);
    category.setDescription(name + " description");
    category.setActive(true);
    entityManager.persist(category);
    return category;
  }

  private Product persistProduct(String sku, String title, Category category) {
    Product product = new Product();
    product.setSku(sku);
    product.setIsbn(sku + "-ISBN");
    product.setTitle(title);
    product.setAuthor("Tester");
    product.setDescription(title + " description");
    product.setPrice(new BigDecimal("25.00"));
    product.setActive(true);
    product.setCategory(category);
    entityManager.persist(product);
    return product;
  }

  private void persistMovement(Product product, InventoryMovementType type, int quantity) {
    InventoryMovement movement = new InventoryMovement();
    movement.setProduct(product);
    movement.setMovementType(type);
    movement.setQuantity(quantity);
    movement.setMovementDate(Instant.now());
    movement.setReference("MOV-" + type + quantity);
    entityManager.persist(movement);
  }
}
