package com.librerialumen.api.web.dto.auth;

import java.time.Instant;
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
public class AuthLoginResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private Instant expiresAt;
}
