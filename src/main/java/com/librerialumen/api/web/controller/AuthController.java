package com.librerialumen.api.web.controller;

import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.AuthMapper;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.security.JwtTokenService;
import com.librerialumen.api.security.JwtTokenService.TokenDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.librerialumen.api.web.dto.auth.AuthLoginRequest;
import com.librerialumen.api.web.dto.auth.AuthLoginResponse;
import com.librerialumen.api.web.dto.auth.MeResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;
  private final UserRepository userRepository;
  private final AuthMapper authMapper;

  @PostMapping("/login")
  @Operation(summary = "Authenticate user and obtain JWT tokens")
  public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    TokenDetails access = jwtTokenService.generateAccessToken(userDetails);
    TokenDetails refresh = jwtTokenService.generateRefreshToken(userDetails);

    AuthLoginResponse response = AuthLoginResponse.builder()
        .accessToken(access.getToken())
        .refreshToken(refresh.getToken())
        .tokenType("Bearer")
        .expiresAt(access.getExpiresAt())
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(summary = "Return profile of the authenticated user", security = { @SecurityRequirement(name = "bearerAuth") })
  public ResponseEntity<MeResponse> currentUser(Principal principal) {
    if (principal == null) {
      throw new BusinessException("AUTH_REQUIRED", "Authentication required");
    }

    return userRepository.findByEmail(principal.getName())
        .map(authMapper::toMeResponse)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
  }
}
