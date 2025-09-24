package com.librerialumen.api.config;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DevDataSeeder {

  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner seedUsers(UserRepository userRepository) {
    return args -> {
      createUserIfMissing(userRepository, "admin@lumen.test", "ADMIN", "Admin", "User");
      createUserIfMissing(userRepository, "employee@lumen.test", "EMPLOYEE", "Employee", "User");
    };
  }

  private void createUserIfMissing(UserRepository userRepository, String email, String role,
      String firstName, String lastName) {
    if (userRepository.existsByEmail(email)) {
      return;
    }
    User user = new User();
    user.setEmail(email.toLowerCase());
    user.setPasswordHash(passwordEncoder.encode("password123"));
    user.setRole(role);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    userRepository.save(user);
  }
}
