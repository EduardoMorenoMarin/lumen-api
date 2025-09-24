package com.librerialumen.api.web.dto.catalog;

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
public class PublicCategoryViewDTO {

  private UUID id;
  private String name;
  private String description;
}
