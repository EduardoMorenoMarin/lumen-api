package com.librerialumen.api.web.dto.auth;

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
public class MeResponse {

  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private String role;
  private Instant createdAt;
}
