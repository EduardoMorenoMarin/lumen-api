package com.librerialumen.api.web.controller;

import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.ReservationService;
import com.librerialumen.api.web.dto.reservation.ReservationCancelRequest;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationPickupRequest;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
@Tag(name = "Reservations", description = "Manage product reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationsController {

  private final ReservationService reservationService;
  private final UserRepository userRepository;

  @PostMapping
  @Operation(summary = "Create reservation", description = "Creates a new reservation for a customer")
  public ReservationViewDTO create(
      @Valid @RequestBody ReservationCreateDTO dto,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    return reservationService.create(dto, actorId);
  }

  @PostMapping("/{id}/accept")
  @Operation(summary = "Accept reservation", description = "Marks a pending reservation as reserved")
  public ReservationViewDTO accept(
      @Parameter(description = "Reservation identifier") @PathVariable("id") UUID id,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    return reservationService.accept(id, actorId);
  }


  @PostMapping("/{id}/confirm")
  @Operation(summary = "Confirm pickup", description = "Marks a reservation as picked up, optionally creating a sale")
  public ReservationViewDTO confirm(
      @Parameter(description = "Reservation identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody ReservationPickupRequest request,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    return reservationService.confirmPickup(id, actorId, request.getCreateSale());
  }

  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel reservation", description = "Cancels an existing reservation with a reason")
  public ReservationViewDTO cancel(
      @Parameter(description = "Reservation identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody ReservationCancelRequest request,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    return reservationService.cancel(id, actorId, request.getReason());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get reservation", description = "Retrieves a reservation by identifier")
  public ReservationViewDTO get(
      @Parameter(description = "Reservation identifier") @PathVariable("id") UUID id) {
    return reservationService.get(id);
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
