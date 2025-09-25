package com.librerialumen.api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.common.WebMvcTestWithAuth;
import com.librerialumen.api.service.CustomerService;
import com.librerialumen.api.web.dto.customer.CustomerCreateDTO;
import com.librerialumen.api.web.dto.customer.CustomerUpdateDTO;
import com.librerialumen.api.web.dto.customer.CustomerViewDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTestWithAuth(controllers = CustomersController.class)
class CustomersControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CustomerService customerService;

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/customers creates customer")
  void createCustomer_shouldReturnCustomer() throws Exception {
    CustomerCreateDTO request = CustomerCreateDTO.builder()
        .firstName("Lola")
        .lastName("Perez")
        .dni("12345678")
        .email("lola@lumen.test")
        .phone("123456789")
        .build();

    CustomerViewDTO response = CustomerViewDTO.builder()
        .id(UUID.randomUUID())
        .firstName("Lola")
        .lastName("Perez")
        .dni("12345678")
        .email("lola@lumen.test")
        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
        .build();

    when(customerService.create(any(CustomerCreateDTO.class))).thenReturn(response);

    mockMvc.perform(post("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId().toString()))
        .andExpect(jsonPath("$.dni").value("12345678"));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("PUT /api/v1/customers/{id} updates customer")
  void updateCustomer_shouldReturnCustomer() throws Exception {
    UUID customerId = UUID.randomUUID();
    CustomerUpdateDTO request = CustomerUpdateDTO.builder()
        .firstName("Lola")
        .lastName("Paz")
        .dni("87654321")
        .email("lola@lumen.test")
        .phone("999111222")
        .build();

    CustomerViewDTO response = CustomerViewDTO.builder()
        .id(customerId)
        .firstName("Lola")
        .lastName("Paz")
        .dni("87654321")
        .email("lola@lumen.test")
        .updatedAt(Instant.parse("2025-01-05T00:00:00Z"))
        .build();

    when(customerService.update(eq(customerId), any(CustomerUpdateDTO.class))).thenReturn(response);

    mockMvc.perform(put("/api/v1/customers/{id}", customerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.dni").value("87654321"));

    verify(customerService).update(eq(customerId), any(CustomerUpdateDTO.class));
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("POST /api/v1/customers validates request body")
  void createCustomer_shouldValidatePayload() throws Exception {
    CustomerCreateDTO invalid = CustomerCreateDTO.builder()
        .firstName("")
        .lastName("")
        .dni("12")
        .email("invalid")
        .phone("abc")
        .build();

    mockMvc.perform(post("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/customers returns list")
  void listCustomers_shouldReturnList() throws Exception {
    CustomerViewDTO customer = CustomerViewDTO.builder()
        .id(UUID.randomUUID())
        .firstName("Juan")
        .lastName("Lopez")
        .dni("87654321")
        .build();
    when(customerService.list()).thenReturn(List.of(customer));

    mockMvc.perform(get("/api/v1/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].dni").value("87654321"));
  }

  @Test
  @WithMockUser(username = "employee@lumen.test", roles = {"EMPLOYEE"})
  @DisplayName("GET /api/v1/customers/{id} returns detail")
  void getCustomer_shouldReturnDetail() throws Exception {
    UUID customerId = UUID.randomUUID();
    CustomerViewDTO customer = CustomerViewDTO.builder()
        .id(customerId)
        .firstName("Mia")
        .lastName("Lee")
        .dni("11223344")
        .build();
    when(customerService.get(customerId)).thenReturn(customer);

    mockMvc.perform(get("/api/v1/customers/{id}", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.firstName").value("Mia"));
  }
}
