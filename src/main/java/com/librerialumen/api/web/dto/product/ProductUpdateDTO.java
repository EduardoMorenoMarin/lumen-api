package com.librerialumen.api.web.dto.product;

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
public class ProductUpdateDTO {

  private UUID id;
  private String sku;
  private String isbn;
  private String title;
  private String author;
  private String description;
  private BigDecimal price;
  private Boolean active;
  private UUID categoryId;
}
