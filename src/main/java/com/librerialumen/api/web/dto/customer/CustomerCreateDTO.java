package com.librerialumen.api.web.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class CustomerCreateDTO {

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @NotBlank
  @Pattern(regexp = "\\d{8}", message = "DNI must contain 8 digits")
  private String dni;

  @Email
  private String email;

  private String phone;

  private String notes;
}
