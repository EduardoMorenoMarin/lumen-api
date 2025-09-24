package com.librerialumen.api.web.dto.sale;

import com.librerialumen.api.domain.enums.SaleStatus;
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
public class SaleViewDTO {

  private UUID id;
  private SaleStatus status;
  private Instant saleDate;
  private BigDecimal totalAmount;
  private BigDecimal taxAmount;
  private BigDecimal discountAmount;
  private String notes;
  private UUID customerId;
  private String customerFirstName;
  private String customerLastName;
  private UUID cashierId;
  private String cashierEmail;
  private List<SaleItemViewDTO> items;
  private Instant createdAt;
  private Instant updatedAt;
}
