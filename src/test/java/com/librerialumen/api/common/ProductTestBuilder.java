package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.domain.model.Product;
import java.math.BigDecimal;
import java.util.UUID;

public final class ProductTestBuilder {

  private UUID id = UUID.randomUUID();
  private String sku = "SKU-001";
  private String isbn = "ISBN-0001";
  private String title = "Sample product";
  private String author = "John Doe";
  private String description = "Product description";
  private BigDecimal price = BigDecimal.valueOf(10.0);
  private boolean active = true;
  private Category category = CategoryTestBuilder.aCategory().build();

  private ProductTestBuilder() {
  }

  public static ProductTestBuilder aProduct() {
    return new ProductTestBuilder();
  }

  public ProductTestBuilder withCategory(Category category) {
    this.category = category;
    return this;
  }

  public ProductTestBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public ProductTestBuilder withSku(String sku) {
    this.sku = sku;
    return this;
  }

  public ProductTestBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public ProductTestBuilder withPrice(BigDecimal price) {
    this.price = price;
    return this;
  }

  public ProductTestBuilder inactive() {
    this.active = false;
    return this;
  }

  public Product build() {
    Product product = new Product();
    product.setId(id);
    product.setSku(sku);
    product.setIsbn(isbn);
    product.setTitle(title);
    product.setAuthor(author);
    product.setDescription(description);
    product.setPrice(price);
    product.setActive(active);
    product.setCategory(category);
    return product;
  }
}

