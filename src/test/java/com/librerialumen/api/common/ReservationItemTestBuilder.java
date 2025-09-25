package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.domain.model.ReservationItem;
import java.math.BigDecimal;
import java.util.UUID;

public final class ReservationItemTestBuilder {

  private UUID id = UUID.randomUUID();
  private Product product = ProductTestBuilder.aProduct().build();
  private int quantity = 1;
  private BigDecimal unitPrice = BigDecimal.TEN;

  private ReservationItemTestBuilder() {
  }

  public static ReservationItemTestBuilder aReservationItem() {
    return new ReservationItemTestBuilder();
  }

  public ReservationItemTestBuilder withProduct(Product product) {
    this.product = product;
    return this;
  }

  public ReservationItemTestBuilder withQuantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public ReservationItemTestBuilder withUnitPrice(BigDecimal price) {
    this.unitPrice = price;
    return this;
  }

  public ReservationItem build() {
    ReservationItem item = new ReservationItem();
    item.setId(id);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setUnitPrice(unitPrice);
    item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    return item;
  }
}

