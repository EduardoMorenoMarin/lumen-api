package com.librerialumen.api.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.mapper.AuthMapper;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.security.JwtTokenService;
import com.librerialumen.api.security.JwtTokenService.TokenDetails;
import com.librerialumen.api.web.dto.auth.AuthLoginRequest;
import com.librerialumen.api.web.dto.auth.AuthLoginResponse;
import com.librerialumen.api.web.dto.auth.MeResponse;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private JwtTokenService jwtTokenService;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private AuthMapper authMapper;

  @Test
  @DisplayName("POST /api/v1/auth/login returns access and refresh tokens")
  void login_shouldReturnTokens() throws Exception {
    AuthLoginRequest request = AuthLoginRequest.builder()
        .email("admin@lumen.test")
        .password("secret123")
        .build();

    UserDetails userDetails = User.withUsername(request.getEmail())
        .password("encoded")
        .authorities("ROLE_ADMIN")
        .build();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    TokenDetails access = new TokenDetails("access-token", Instant.parse("2025-12-31T10:15:30Z"));
    TokenDetails refresh = new TokenDetails("refresh-token", Instant.parse("2026-12-31T10:15:30Z"));

    when(jwtTokenService.generateAccessToken(userDetails)).thenReturn(access);
    when(jwtTokenService.generateRefreshToken(userDetails)).thenReturn(refresh);

    MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    AuthLoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
        AuthLoginResponse.class);

    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());
    assertEquals("Bearer", response.getTokenType());
    assertEquals(Instant.parse("2025-12-31T10:15:30Z"), response.getExpiresAt());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtTokenService).generateAccessToken(userDetails);
    verify(jwtTokenService).generateRefreshToken(userDetails);
  }

  @Test
  @DisplayName("POST /api/v1/auth/login propagates BadCredentialsException")
  void login_shouldPropagateBadCredentials() {
    AuthLoginRequest request = AuthLoginRequest.builder()
        .email("admin@lumen.test")
        .password("bad")
        .build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("bad"));

    Exception exception = assertThrows(Exception.class, () ->
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized()));

    Throwable cause = exception;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    assertTrue(cause instanceof BadCredentialsException);
  }

  @Test
  @DisplayName("GET /api/v1/auth/me returns current user profile")
  void currentUser_shouldReturnProfile() throws Exception {
    UUID userId = UUID.randomUUID();
    com.librerialumen.api.domain.model.User user = new com.librerialumen.api.domain.model.User();
    user.setId(userId);
    user.setEmail("admin@lumen.test");
    user.setRole("ADMIN");

    MeResponse response = MeResponse.builder()
        .id(userId)
        .email(user.getEmail())
        .role(user.getRole())
        .build();

    when(userRepository.findByEmail("admin@lumen.test")).thenReturn(Optional.of(user));
    when(authMapper.toMeResponse(user)).thenReturn(response);

    MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/api/v1/auth/me")
            .principal(() -> "admin@lumen.test"))
        .andExpect(status().isOk())
        .andReturn();

    MeResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), MeResponse.class);
    assertEquals(userId, body.getId());
    assertEquals("admin@lumen.test", body.getEmail());
    assertEquals("ADMIN", body.getRole());

    verify(userRepository).findByEmail("admin@lumen.test");
    verify(authMapper).toMeResponse(user);
  }
}
