package com.librerialumen.api.web.controller;

import com.librerialumen.api.repository.projection.DailySalesTotals;
import com.librerialumen.api.repository.projection.WeeklySalesTotals;
import com.librerialumen.api.service.ReportService;
import com.librerialumen.api.web.dto.report.DailySalesTotalsDTO;
import com.librerialumen.api.web.dto.report.WeeklySalesTotalsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "Analytical reports and metrics")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

  private final ReportService reportService;

  @GetMapping("/sales/daily")
  @Operation(summary = "Daily sales totals", description = "Returns aggregated sales totals for the selected range")
  public List<DailySalesTotalsDTO> getDailySalesTotals(
      @Parameter(description = "Start of the range in ISO date-time format")
      @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @Parameter(description = "End of the range in ISO date-time format")
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
    List<DailySalesTotals> totals = reportService.getSalesTotals(start, end);
    return totals.stream()
        .map(total -> DailySalesTotalsDTO.builder()
            .day(total.getDay())
            .totalAmount(total.getTotalAmount())
            .taxAmount(total.getTaxAmount())
            .discountAmount(total.getDiscountAmount())
            .build())
        .collect(Collectors.toList());
  }

  @GetMapping("/sales/weekly")
  @Operation(summary = "Weekly sales totals", description = "Returns aggregated sales totals grouped by week for the selected range")
  public List<WeeklySalesTotalsDTO> getWeeklySalesTotals(
      @Parameter(description = "Start of the range in ISO date-time format")
      @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @Parameter(description = "End of the range in ISO date-time format")
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
    List<WeeklySalesTotals> totals = reportService.getWeeklySalesTotals(start, end);
    return totals.stream()
        .map(total -> WeeklySalesTotalsDTO.builder()
            .weekStart(total.getWeekStart())
            .weekEnd(total.getWeekEnd())
            .totalAmount(total.getTotalAmount())
            .taxAmount(total.getTaxAmount())
            .discountAmount(total.getDiscountAmount())
            .build())
        .collect(Collectors.toList());
  }
}
