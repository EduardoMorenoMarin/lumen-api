package com.librerialumen.api.common;

import com.librerialumen.api.security.JwtAuthenticationFilter;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Provides a Mockito mock of {@link JwtAuthenticationFilter} for MVC slice tests. Import this configuration only in
 * controller tests; integration tests should rely on the real filter to exercise the full security stack.
 */
@TestConfiguration
public class MockJwtFilterConfiguration {

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return Mockito.mock(JwtAuthenticationFilter.class);
  }
}

