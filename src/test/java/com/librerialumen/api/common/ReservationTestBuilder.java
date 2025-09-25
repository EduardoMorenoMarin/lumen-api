package com.librerialumen.api.common;

import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.domain.model.Reservation;
import com.librerialumen.api.domain.model.ReservationItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ReservationTestBuilder {

  private UUID id = UUID.randomUUID();
  private String code = "RSV-1234";
  private ReservationStatus status = ReservationStatus.PENDING;
  private Instant reservationDate = Instant.now();
  private Instant expirationDate = reservationDate.plusSeconds(86400);
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private String notes = null;
  private Customer customer = CustomerTestBuilder.aCustomer().build();
  private final List<ReservationItem> items = new ArrayList<>();

  private ReservationTestBuilder() {
  }

  public static ReservationTestBuilder aReservation() {
    return new ReservationTestBuilder();
  }

  public ReservationTestBuilder withCustomer(Customer customer) {
    this.customer = customer;
    return this;
  }

  public ReservationTestBuilder withStatus(ReservationStatus status) {
    this.status = status;
    return this;
  }

  public ReservationTestBuilder withNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public ReservationTestBuilder withItem(ReservationItem item) {
    this.items.add(item);
    this.totalAmount = this.totalAmount.add(item.getTotalPrice());
    return this;
  }

  public Reservation build() {
    Reservation reservation = new Reservation();
    reservation.setId(id);
    reservation.setCode(code);
    reservation.setStatus(status);
    reservation.setReservationDate(reservationDate);
    reservation.setExpirationDate(expirationDate);
    reservation.setTotalAmount(totalAmount);
    reservation.setNotes(notes);
    reservation.setCustomer(customer);

    List<ReservationItem> clonedItems = new ArrayList<>();
    for (ReservationItem item : items) {
      ReservationItem clone = item;
      clone.setReservation(reservation);
      clonedItems.add(clone);
    }
    reservation.setItems(clonedItems);
    return reservation;
  }
}

