package com.librerialumen.api.web.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class WeeklySalesTotalsDTO {

  private LocalDate weekStart;
  private LocalDate weekEnd;
  private BigDecimal totalAmount;
  private BigDecimal taxAmount;
  private BigDecimal discountAmount;
}
