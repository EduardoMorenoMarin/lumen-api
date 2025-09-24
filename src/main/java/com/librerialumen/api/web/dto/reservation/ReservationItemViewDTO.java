package com.librerialumen.api.web.dto.reservation;

import java.math.BigDecimal;
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
public class ReservationItemViewDTO {

  private UUID id;
  private UUID productId;
  private String productTitle;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
}
