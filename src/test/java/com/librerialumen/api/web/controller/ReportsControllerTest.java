package com.librerialumen.api.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.librerialumen.api.common.WebMvcTestWithAuth;
import com.librerialumen.api.repository.projection.DailySalesTotals;
import com.librerialumen.api.repository.projection.WeeklySalesTotals;
import com.librerialumen.api.service.ReportService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTestWithAuth(controllers = ReportsController.class)
class ReportsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ReportService reportService;

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("GET /api/v1/reports/sales/daily returns daily totals")
  void getDailySalesTotals_shouldReturnTotals() throws Exception {
    DailySalesTotals projection = new DailySalesTotals() {
      @Override
      public LocalDate getDay() {
        return LocalDate.of(2025, 1, 1);
      }

      @Override
      public BigDecimal getTotalAmount() {
        return BigDecimal.valueOf(150.75);
      }

      @Override
      public BigDecimal getTaxAmount() {
        return BigDecimal.valueOf(15.07);
      }

      @Override
      public BigDecimal getDiscountAmount() {
        return BigDecimal.valueOf(5.00);
      }
    };

    when(reportService.getSalesTotals(any(Instant.class), any(Instant.class)))
        .thenReturn(List.of(projection));

    mockMvc.perform(get("/api/v1/reports/sales/daily")
            .param("start", "2025-01-01T00:00:00Z")
            .param("end", "2025-01-31T23:59:59Z"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].day").value("2025-01-01"))
        .andExpect(jsonPath("$[0].totalAmount").value(150.75))
        .andExpect(jsonPath("$[0].taxAmount").value(15.07))
        .andExpect(jsonPath("$[0].discountAmount").value(5.0));
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("GET /api/v1/reports/sales/daily requires start and end parameters")
  void getDailySalesTotals_shouldValidateParams() throws Exception {
    mockMvc.perform(get("/api/v1/reports/sales/daily")
            .param("start", "2025-01-01T00:00:00Z"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  @WithMockUser(username = "admin@lumen.test", roles = {"ADMIN"})
  @DisplayName("GET /api/v1/reports/sales/weekly returns weekly totals")
  void getWeeklySalesTotals_shouldReturnTotals() throws Exception {
    WeeklySalesTotals projection = new WeeklySalesTotals() {
      @Override
      public LocalDate getWeekStart() {
        return LocalDate.of(2025, 1, 6);
      }

      @Override
      public LocalDate getWeekEnd() {
        return LocalDate.of(2025, 1, 12);
      }

      @Override
      public BigDecimal getTotalAmount() {
        return BigDecimal.valueOf(700);
      }

      @Override
      public BigDecimal getTaxAmount() {
        return BigDecimal.valueOf(70);
      }

      @Override
      public BigDecimal getDiscountAmount() {
        return BigDecimal.valueOf(20);
      }
    };

    when(reportService.getWeeklySalesTotals(any(Instant.class), any(Instant.class)))
        .thenReturn(List.of(projection));

    mockMvc.perform(get("/api/v1/reports/sales/weekly")
            .param("start", "2025-01-01T00:00:00Z")
            .param("end", "2025-01-31T23:59:59Z"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].weekStart").value("2025-01-06"))
        .andExpect(jsonPath("$[0].weekEnd").value("2025-01-12"))
        .andExpect(jsonPath("$[0].totalAmount").value(700))
        .andExpect(jsonPath("$[0].taxAmount").value(70))
        .andExpect(jsonPath("$[0].discountAmount").value(20));
  }
}
