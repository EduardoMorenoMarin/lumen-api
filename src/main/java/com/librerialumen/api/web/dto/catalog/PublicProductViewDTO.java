package com.librerialumen.api.web.dto.catalog;

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
public class PublicProductViewDTO {

  private UUID id;
  private String title;
  private String author;
  private BigDecimal price;
  private UUID categoryId;
  private String categoryName;
  private Integer stock;
}
