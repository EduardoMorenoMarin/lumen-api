package com.librerialumen.api.web.dto.sale;

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
public class SaleCreateDTO {

  private List<SaleItemCreateDTO> items;
  private String paymentMethod;
  private UUID customerId;
}

