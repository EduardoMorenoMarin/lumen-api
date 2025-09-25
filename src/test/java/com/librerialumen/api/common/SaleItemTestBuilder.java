package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.domain.model.SaleItem;
import java.math.BigDecimal;
import java.util.UUID;

public final class SaleItemTestBuilder {

  private UUID id = UUID.randomUUID();
  private Product product = ProductTestBuilder.aProduct().build();
  private int quantity = 1;
  private BigDecimal unitPrice = BigDecimal.TEN;

  private SaleItemTestBuilder() {
  }

  public static SaleItemTestBuilder aSaleItem() {
    return new SaleItemTestBuilder();
  }

  public SaleItemTestBuilder withProduct(Product product) {
    this.product = product;
    return this;
  }

  public SaleItemTestBuilder withQuantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public SaleItemTestBuilder withUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  public SaleItem build() {
    SaleItem item = new SaleItem();
    item.setId(id);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setUnitPrice(unitPrice);
    item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    return item;
  }
}

