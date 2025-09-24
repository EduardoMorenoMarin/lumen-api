package com.librerialumen.api.domain.model;

import com.librerialumen.api.domain.enums.ReservationStatus;
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
import jakarta.persistence.UniqueConstraint;
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
    name = "reservations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reservations_code", columnNames = {"code"})
    },
    indexes = {
        @Index(name = "idx_reservations_code", columnList = "code")
    }
)
public class Reservation extends BaseEntity {

  @Column(name = "code", nullable = false, length = 60)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ReservationStatus status = ReservationStatus.PENDING;

  @Column(name = "reservation_date", nullable = false)
  private Instant reservationDate;

  @Column(name = "expiration_date")
  private Instant expirationDate;

  @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "notes", length = 500)
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @OneToMany(
      mappedBy = "reservation",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<ReservationItem> items = new ArrayList<>();
}
