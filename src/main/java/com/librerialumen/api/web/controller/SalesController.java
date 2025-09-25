package com.librerialumen.api.web.controller;

import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.SaleService;
import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Sales", description = "Sales lifecycle operations")
@SecurityRequirement(name = "bearerAuth")
public class SalesController {

  private final SaleService saleService;
  private final UserRepository userRepository;

  @PostMapping
  @Operation(summary = "Create sale", description = "Registers a new sale for the authenticated user")
  public SaleViewDTO create(
      @Valid @RequestBody SaleCreateDTO dto,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    return saleService.create(dto, actorId);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get sale", description = "Retrieves a sale by identifier")
  public SaleViewDTO get(
      @Parameter(description = "Sale identifier") @PathVariable("id") UUID id) {
    return saleService.get(id);
  }

  @GetMapping
  @Operation(summary = "List sales by date", description = "Returns sales within the provided date range")
  public List<SaleViewDTO> listByDate(
      @Parameter(description = "Start of the range in ISO date-time format")
      @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @Parameter(description = "End of the range in ISO date-time format")
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
    return saleService.getDailySales(start, end);
  }

  private UUID resolveActorId(Principal principal) {
    if (principal == null) {
      throw new BusinessException("AUTH_REQUIRED", "Authentication required");
    }
    return userRepository.findByEmail(principal.getName())
        .map(com.librerialumen.api.domain.model.User::getId)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Authenticated user not found"));
  }
}
