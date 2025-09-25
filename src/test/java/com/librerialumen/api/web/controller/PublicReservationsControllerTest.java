package com.librerialumen.api.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.common.MockJwtFilterConfiguration;
import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.service.ReservationService;
import com.librerialumen.api.web.dto.reservation.PublicReservationCreateRequest;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationCustomerDataDTO;
import com.librerialumen.api.web.dto.reservation.ReservationItemCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublicReservationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockJwtFilterConfiguration.class)
class PublicReservationsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReservationService reservationService;

  @Test
  @DisplayName("POST /public/reservations validates payload and returns reservation")
  void createReservation_shouldReturnReservation() throws Exception {
    ReservationItemCreateDTO item = ReservationItemCreateDTO.builder()
        .productId(UUID.randomUUID())
        .quantity(1)
        .build();
    ReservationCustomerDataDTO customer = ReservationCustomerDataDTO.builder()
        .dni("12345678")
        .firstName("Ana")
        .lastName("Doe")
        .email("ana@lumen.test")
        .phone("123456789")
        .build();

    List<ReservationItemCreateDTO> items = List.of(item);

    PublicReservationCreateRequest request = PublicReservationCreateRequest.builder()
        .customerData(customer)
        .items(items)
        .pickupDeadline(Instant.parse("2025-12-31T10:15:30Z"))
        .notes("Please confirm")
        .build();

    ReservationViewDTO response = ReservationViewDTO.builder()
        .id(UUID.randomUUID())
        .status(ReservationStatus.PENDING)
        .totalAmount(BigDecimal.valueOf(50))
        .build();

    when(reservationService.create(any(ReservationCreateDTO.class), isNull())).thenReturn(response);

    mockMvc.perform(post("/public/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.totalAmount").value(50));

    ArgumentCaptor<ReservationCreateDTO> captor = ArgumentCaptor.forClass(ReservationCreateDTO.class);
    verify(reservationService).create(captor.capture(), isNull());

    ReservationCreateDTO payload = captor.getValue();
    assertEquals(customer.getDni(), payload.getCustomerData().getDni());
    assertEquals(customer.getFirstName(), payload.getCustomerData().getFirstName());
    assertEquals(customer.getLastName(), payload.getCustomerData().getLastName());
    assertEquals(items.get(0).getProductId(), payload.getItems().get(0).getProductId());
    assertEquals(items.get(0).getQuantity(), payload.getItems().get(0).getQuantity());
    assertEquals(request.getPickupDeadline(), payload.getPickupDeadline());
    assertEquals(request.getNotes(), payload.getNotes());
  }

  @Test
  @DisplayName("POST /public/reservations returns 400 when validation fails")
  void createReservation_shouldReturnBadRequestOnInvalidPayload() throws Exception {
    ReservationCustomerDataDTO invalidCustomer = ReservationCustomerDataDTO.builder()
        .dni("123")
        .firstName("Ana")
        .lastName("Doe")
        .email("invalid-email")
        .phone("abc")
        .build();

    PublicReservationCreateRequest invalidRequest = PublicReservationCreateRequest.builder()
        .customerData(invalidCustomer)
        .items(List.of(ReservationItemCreateDTO.builder()
            .productId(null)
            .quantity(0)
            .build()))
        .pickupDeadline(null)
        .build();

    mockMvc.perform(post("/public/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }
}
