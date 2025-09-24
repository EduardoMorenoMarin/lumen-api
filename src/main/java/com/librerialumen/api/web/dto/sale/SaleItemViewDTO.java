package com.librerialumen.api.web.dto.sale;

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
public class SaleItemViewDTO {

  private UUID id;
  private UUID productId;
  private String productTitle;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
}
