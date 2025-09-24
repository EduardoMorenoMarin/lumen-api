package com.librerialumen.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

  @Column(name = "first_name", length = 120)
  private String firstName;

  @Column(name = "last_name", length = 120)
  private String lastName;

  @Column(name = "dni", length = 16, unique = true)
  private String dni;

  @Column(name = "email", length = 320)
  private String email;

  @Column(name = "phone", length = 50)
  private String phone;

  @Column(name = "notes", length = 500)
  private String notes;
}
