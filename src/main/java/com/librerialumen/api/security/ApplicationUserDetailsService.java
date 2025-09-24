package com.librerialumen.api.security;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!user.isActive()) {
      throw new UsernameNotFoundException("User is inactive");
    }

    String role = user.getRole() != null ? user.getRole().toUpperCase() : "EMPLOYEE";
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .authorities(authorities)
        .accountLocked(false)
        .accountExpired(false)
        .credentialsExpired(false)
        .disabled(!user.isActive())
        .build();
  }
}
