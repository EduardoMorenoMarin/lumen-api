package com.librerialumen.api.security;

import com.librerialumen.api.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

  private final JwtProperties properties;
  private final SecretKey signingKey;

  public JwtTokenService(JwtProperties properties) {
    this.properties = properties;
    this.signingKey = buildSigningKey(properties.getSecret());
  }

  public TokenDetails generateAccessToken(UserDetails user) {
    return generateToken(createClaims(user), user, properties.getExpiration());
  }

  public TokenDetails generateRefreshToken(UserDetails user) {
    return generateToken(createClaims(user), user, properties.getRefreshExpiration());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Instant extractExpiration(String token) {
    return extractClaim(token, claims -> claims.getExpiration().toInstant());
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).isBefore(Instant.now());
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private TokenDetails generateToken(Map<String, Object> extraClaims, UserDetails userDetails,
      Duration validity) {
    Instant now = Instant.now();
    Instant expiration = now.plus(validity);
    String token = Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .signWith(signingKey, Jwts.SIG.HS256)
        .compact();
    return new TokenDetails(token, expiration);
  }

  private Map<String, Object> createClaims(UserDetails user) {
    List<String> roles = user.getAuthorities().stream()
        .map(grantedAuthority -> grantedAuthority.getAuthority())
        .collect(Collectors.toList());
    return Map.of("roles", roles);
  }

  private SecretKey buildSigningKey(String secret) {
    if (secret == null || secret.isBlank()) {
      secret = "change-me-to-a-strong-secret";
    }
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      keyBytes = Arrays.copyOf(keyBytes, 32);
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Getter
  public static class TokenDetails {

    private final String token;
    private final Instant expiresAt;

    public TokenDetails(String token, Instant expiresAt) {
      this.token = token;
      this.expiresAt = expiresAt;
    }
  }
}
