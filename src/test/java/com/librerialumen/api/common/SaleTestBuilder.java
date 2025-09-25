package com.librerialumen.api.common;

import com.librerialumen.api.domain.enums.SaleStatus;
import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.domain.model.Sale;
import com.librerialumen.api.domain.model.SaleItem;
import com.librerialumen.api.domain.model.User;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SaleTestBuilder {

  private UUID id = UUID.randomUUID();
  private SaleStatus status = SaleStatus.COMPLETED;
  private Instant saleDate = Instant.now();
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private BigDecimal taxAmount = BigDecimal.ZERO;
  private BigDecimal discountAmount = BigDecimal.ZERO;
  private String notes = "Paid in cash";
  private User cashier = UserTestBuilder.aUser().withRole("ADMIN").build();
  private Customer customer = CustomerTestBuilder.aCustomer().build();
  private final List<SaleItem> items = new ArrayList<>();

  private SaleTestBuilder() {
  }

  public static SaleTestBuilder aSale() {
    return new SaleTestBuilder();
  }

  public SaleTestBuilder withCashier(User cashier) {
    this.cashier = cashier;
    return this;
  }

  public SaleTestBuilder withCustomer(Customer customer) {
    this.customer = customer;
    return this;
  }

  public SaleTestBuilder withItem(SaleItem item) {
    this.items.add(item);
    this.totalAmount = this.totalAmount.add(item.getTotalPrice());
    return this;
  }

  public Sale build() {
    Sale sale = new Sale();
    sale.setId(id);
    sale.setStatus(status);
    sale.setSaleDate(saleDate);
    sale.setTotalAmount(totalAmount);
    sale.setTaxAmount(taxAmount);
    sale.setDiscountAmount(discountAmount);
    sale.setNotes(notes);
    sale.setCashier(cashier);
    sale.setCustomer(customer);

    List<SaleItem> clonedItems = new ArrayList<>();
    for (SaleItem item : items) {
      SaleItem clone = item;
      clone.setSale(sale);
      clonedItems.add(clone);
    }
    sale.setItems(clonedItems);
    return sale;
  }
}

