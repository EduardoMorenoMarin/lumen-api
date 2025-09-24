package com.librerialumen.api.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

  private String secret = "change-me";
  private Duration expiration = Duration.ofHours(1);
  private Duration refreshExpiration = Duration.ofHours(8);
}
