package com.librerialumen.api.web.dto.reservation;

import com.librerialumen.api.domain.enums.ReservationStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationViewDTO {

  private UUID id;
  private String code;
  private ReservationStatus status;
  private Instant reservationDate;
  private Instant pickupDeadline;
  private BigDecimal totalAmount;
  private String notes;
  private UUID customerId;
  private String customerFirstName;
  private String customerLastName;
  private String customerDni;
  private String customerEmail;
  private String customerPhone;
  private List<ReservationItemViewDTO> items;
  private Instant createdAt;
  private Instant updatedAt;
}
