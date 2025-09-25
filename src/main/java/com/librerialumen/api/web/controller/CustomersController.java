package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.CustomerService;
import com.librerialumen.api.web.dto.customer.CustomerCreateDTO;
import com.librerialumen.api.web.dto.customer.CustomerUpdateDTO;
import com.librerialumen.api.web.dto.customer.CustomerViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Customers", description = "Customer directory management")
@SecurityRequirement(name = "bearerAuth")
public class CustomersController {

  private final CustomerService customerService;

  @PostMapping
  @Operation(summary = "Create customer", description = "Registers a new customer")
  public CustomerViewDTO create(@Valid @RequestBody CustomerCreateDTO dto) {
    return customerService.create(dto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update customer", description = "Updates an existing customer")
  public CustomerViewDTO update(
      @Parameter(description = "Customer identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody CustomerUpdateDTO dto) {
    return customerService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Patch customer", description = "Applies a partial update to an existing customer")
  public CustomerViewDTO patch(
      @Parameter(description = "Customer identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody CustomerUpdateDTO dto) {
    return customerService.patch(id, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete customer", description = "Removes a customer by identifier")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Customer identifier") @PathVariable("id") UUID id) {
    customerService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "List customers", description = "Returns all registered customers")
  public List<CustomerViewDTO> list() {
    return customerService.list();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get customer", description = "Fetches a customer by identifier")
  public CustomerViewDTO get(
      @Parameter(description = "Customer identifier") @PathVariable("id") UUID id) {
    return customerService.get(id);
  }
}
