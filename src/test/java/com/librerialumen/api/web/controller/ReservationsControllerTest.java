package com.librerialumen.api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.common.WebMvcTestWithAuth;
import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.ReservationService;
import com.librerialumen.api.web.dto.reservation.ReservationCancelRequest;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationItemCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationPickupRequest;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTestWithAuth(controllers = ReservationsController.class)
class ReservationsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReservationService reservationService;

  @MockitoBean
  private UserRepository userRepository;

  private UUID actorId;

  @BeforeEach
  void setUp() {
    actorId = UUID.randomUUID();
    User user = new User();
    user.setId(actorId);
    user.setEmail("admin@lumen.test");
    user.setRole("ADMIN");
    when(userRepository.findByEmail("admin@lumen.test")).thenReturn(Optional.of(user));
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/reservations creates reservation with actor")
  void create_shouldForwardToService() throws Exception {
    ReservationCreateDTO request = ReservationCreateDTO.builder()
        .customerId(UUID.randomUUID())
        .items(List.of(ReservationItemCreateDTO.builder()
            .productId(UUID.randomUUID())
            .quantity(1)
            .build()))
        .pickupDeadline(Instant.parse("2025-12-31T10:15:30Z"))
        .notes("note")
        .build();

    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(UUID.randomUUID())
        .status(com.librerialumen.api.domain.enums.ReservationStatus.PENDING)
        .build();

    when(reservationService.create(any(ReservationCreateDTO.class), eq(actorId))).thenReturn(response);

    mockMvc.perform(post("/api/v1/reservations")
            .principal(() -> "admin@lumen.test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.status").value("PENDING"));

    verify(reservationService).create(any(ReservationCreateDTO.class), eq(actorId));
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/reservations/{id}/accept delegates to service")
  void accept_shouldUseActor() throws Exception {
    UUID reservationId = UUID.randomUUID();
    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(reservationId)
        .status(com.librerialumen.api.domain.enums.ReservationStatus.RESERVED)
        .build();
    when(reservationService.accept(reservationId, actorId)).thenReturn(response);

    mockMvc.perform(post("/api/v1/reservations/{id}/accept", reservationId)
            .principal(() -> "admin@lumen.test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("RESERVED"));

    verify(reservationService).accept(reservationId, actorId);
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/reservations/{id}/confirm handles createSale flag")
  void confirm_shouldPassCreateSaleFlag() throws Exception {
    UUID reservationId = UUID.randomUUID();
    ReservationPickupRequest request = ReservationPickupRequest.builder()
        .createSale(true)
        .build();
    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(reservationId)
        .status(com.librerialumen.api.domain.enums.ReservationStatus.COMPLETED)
        .build();
    when(reservationService.confirmPickup(reservationId, actorId, true)).thenReturn(response);

    mockMvc.perform(post("/api/v1/reservations/{id}/confirm", reservationId)
            .principal(() -> "admin@lumen.test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"));

    verify(reservationService).confirmPickup(reservationId, actorId, true);
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/reservations/{id}/cancel sends reason to service")
  void cancel_shouldSendReason() throws Exception {
    UUID reservationId = UUID.randomUUID();
    ReservationCancelRequest request = ReservationCancelRequest.builder()
        .reason("Customer no-show")
        .build();
    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(reservationId)
        .status(com.librerialumen.api.domain.enums.ReservationStatus.CANCELLED)
        .build();
    when(reservationService.cancel(reservationId, actorId, "Customer no-show")).thenReturn(response);

    mockMvc.perform(post("/api/v1/reservations/{id}/cancel", reservationId)
            .principal(() -> "admin@lumen.test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));

    verify(reservationService).cancel(reservationId, actorId, "Customer no-show");
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("GET /api/v1/reservations/{id} returns reservation data")
  void get_shouldReturnReservation() throws Exception {
    UUID reservationId = UUID.randomUUID();
    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(reservationId)
        .status(com.librerialumen.api.domain.enums.ReservationStatus.PENDING)
        .build();
    when(reservationService.get(reservationId)).thenReturn(response);

    mockMvc.perform(get("/api/v1/reservations/{id}", reservationId)
            .principal(() -> "admin@lumen.test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reservationId.toString()));
  }
}
