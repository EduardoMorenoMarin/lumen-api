package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.User;
import java.util.UUID;

public final class UserTestBuilder {

  private UUID id = UUID.randomUUID();
  private String email = "user@lumen.test";
  private String passwordHash = "encoded-password";
  private String firstName = "Test";
  private String lastName = "User";
  private String role = "EMPLOYEE";
  private boolean active = true;

  private UserTestBuilder() {
  }

  public static UserTestBuilder aUser() {
    return new UserTestBuilder();
  }

  public UserTestBuilder withRole(String role) {
    this.role = role;
    return this;
  }

  public UserTestBuilder inactive() {
    this.active = false;
    return this;
  }

  public User build() {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setRole(role);
    user.setActive(active);
    return user;
  }
}

