package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.ReservationService;
import com.librerialumen.api.web.dto.reservation.PublicReservationCreateRequest;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/reservations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public Reservations", description = "Public reservation entrypoint")
public class PublicReservationsController {

  private final ReservationService reservationService;

  @PostMapping
  @Operation(summary = "Create reservation", description = "Allows customers to create a reservation without authentication")
  public ReservationViewDTO create(@Valid @RequestBody PublicReservationCreateRequest request) {
    ReservationCreateDTO dto = ReservationCreateDTO.builder()
        .customerData(request.getCustomerData())
        .items(request.getItems())
        .pickupDeadline(request.getPickupDeadline())
        .notes(request.getNotes())
        .build();
    return reservationService.create(dto, null);
  }
}
