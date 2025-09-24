package com.librerialumen.api.domain.model;

import com.librerialumen.api.domain.enums.InventoryMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventory_movements")
public class InventoryMovement extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "movement_type", nullable = false, length = 20)
  private InventoryMovementType movementType;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "movement_date", nullable = false)
  private Instant movementDate;

  @Column(name = "reference", length = 120)
  private String reference;

  @Column(name = "notes", length = 500)
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
