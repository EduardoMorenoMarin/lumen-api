package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.Customer;
import java.util.UUID;

public final class CustomerTestBuilder {

  private UUID id = UUID.randomUUID();
  private String dni = "12345678";
  private String firstName = "Jane";
  private String lastName = "Doe";
  private String email = "jane.doe@example.com";
  private String phone = "+54 11 1234 5678";

  private CustomerTestBuilder() {
  }

  public static CustomerTestBuilder aCustomer() {
    return new CustomerTestBuilder();
  }

  public CustomerTestBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public CustomerTestBuilder withDni(String dni) {
    this.dni = dni;
    return this;
  }

  public CustomerTestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public Customer build() {
    Customer customer = new Customer();
    customer.setId(id);
    customer.setDni(dni);
    customer.setFirstName(firstName);
    customer.setLastName(lastName);
    customer.setEmail(email);
    customer.setPhone(phone);
    return customer;
  }
}

