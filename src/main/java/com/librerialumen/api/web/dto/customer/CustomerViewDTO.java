package com.librerialumen.api.web.dto.customer;

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
public class CustomerViewDTO {

  private UUID id;
  private String firstName;
  private String lastName;
  private String dni;
  private String email;
  private String phone;
  private String notes;
  private Instant createdAt;
  private Instant updatedAt;
}
