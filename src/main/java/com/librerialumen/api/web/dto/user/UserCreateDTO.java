package com.librerialumen.api.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserCreateDTO {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, max = 255)
  private String password;

  @NotBlank
  private String role;

  private String firstName;

  private String lastName;

  private Boolean active;
}
