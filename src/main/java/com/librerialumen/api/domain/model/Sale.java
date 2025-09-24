package com.librerialumen.api.domain.model;

import com.librerialumen.api.domain.enums.SaleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "sales",
    indexes = {
        @Index(name = "idx_sales_created_at", columnList = "created_at")
    }
)
public class Sale extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private SaleStatus status = SaleStatus.PENDING;

  @Column(name = "sale_date", nullable = false)
  private Instant saleDate;

  @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "tax_amount", precision = 19, scale = 2)
  private BigDecimal taxAmount;

  @Column(name = "discount_amount", precision = 19, scale = 2)
  private BigDecimal discountAmount;

  @Column(name = "notes", length = 500)
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cashier_id", nullable = false)
  private User cashier;

  @OneToMany(
      mappedBy = "sale",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<SaleItem> items = new ArrayList<>();
}
