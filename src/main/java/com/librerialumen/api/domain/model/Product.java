package com.librerialumen.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_sku", columnNames = {"sku"}),
        @UniqueConstraint(name = "uk_products_isbn", columnNames = {"isbn"})
    },
    indexes = {
        @Index(name = "idx_products_sku", columnList = "sku"),
        @Index(name = "idx_products_title", columnList = "title")
    }
)
public class Product extends BaseEntity {

  @Column(name = "sku", nullable = false, length = 50)
  private String sku;

  @Column(name = "isbn", length = 20)
  private String isbn;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "author", length = 150)
  private String author;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "price", precision = 19, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
}
