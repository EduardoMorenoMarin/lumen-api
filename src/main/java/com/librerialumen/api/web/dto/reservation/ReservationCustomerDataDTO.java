package com.librerialumen.api.web.dto.reservation;

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
public class ReservationCustomerDataDTO {

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @NotBlank
  @Pattern(regexp = "\\d{8}", message = "DNI must contain 8 digits")
  private String dni;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Pattern(regexp = "^[0-9+\\-()\\s]{7,20}$", message = "Phone number is not valid")
  private String phone;
}
