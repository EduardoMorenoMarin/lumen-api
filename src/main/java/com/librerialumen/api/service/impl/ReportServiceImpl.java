package com.librerialumen.api.service.impl;

import com.librerialumen.api.repository.SaleRepository;
import com.librerialumen.api.repository.projection.DailySalesTotals;
import com.librerialumen.api.repository.projection.WeeklySalesTotals;
import com.librerialumen.api.service.ReportService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

  private final SaleRepository saleRepository;

  @Override
  public List<DailySalesTotals> getSalesTotals(Instant start, Instant end) {
    return saleRepository.findDailyTotals(start, end);
  }

  @Override
  public List<WeeklySalesTotals> getWeeklySalesTotals(Instant start, Instant end) {
    return saleRepository.findWeeklyTotals(start, end);
  }
}
