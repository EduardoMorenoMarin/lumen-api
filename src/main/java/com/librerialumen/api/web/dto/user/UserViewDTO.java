package com.librerialumen.api.web.dto.user;

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
public class UserViewDTO {

  private UUID id;
  private String email;
  private String role;
  private String firstName;
  private String lastName;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
