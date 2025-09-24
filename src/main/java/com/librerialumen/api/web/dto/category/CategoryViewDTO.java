package com.librerialumen.api.web.dto.category;

import java.time.Instant;
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
public class CategoryViewDTO {

  private UUID id;
  private String name;
  private String description;
  private Boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
