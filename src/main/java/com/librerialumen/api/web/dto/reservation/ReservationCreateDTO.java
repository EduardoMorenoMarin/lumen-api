package com.librerialumen.api.web.dto.reservation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ReservationCreateDTO {

  private UUID customerId;

  @Valid
  private ReservationCustomerDataDTO customerData;

  @NotEmpty
  private List<@Valid ReservationItemCreateDTO> items;

  @NotNull
  private Instant pickupDeadline;

  private String notes;
}
