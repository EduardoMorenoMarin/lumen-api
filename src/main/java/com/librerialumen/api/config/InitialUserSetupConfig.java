package com.librerialumen.api.config;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class InitialUserSetupConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitialUserSetupConfig.class);
  private static final String DEFAULT_EMAIL = "admin@lumen.local";
  private static final String DEFAULT_PASSWORD = "admin123";

  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner ensureDefaultUser(UserRepository userRepository) {
    return args -> {
      if (userRepository.count() > 0) {
        return;
      }

      User admin = new User();
      admin.setEmail(DEFAULT_EMAIL);
      admin.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
      admin.setFirstName("Default");
      admin.setLastName("Admin");
      admin.setRole("ADMIN");

      userRepository.save(admin);
      LOGGER.warn("No users found in database. Created default admin user '{}' with preset password.", DEFAULT_EMAIL);
    };
  }
}
