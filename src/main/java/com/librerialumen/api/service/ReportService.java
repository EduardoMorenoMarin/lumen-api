package com.librerialumen.api.service;

import com.librerialumen.api.repository.projection.DailySalesTotals;
import com.librerialumen.api.repository.projection.WeeklySalesTotals;
import java.time.Instant;
import java.util.List;

public interface ReportService {

  List<DailySalesTotals> getSalesTotals(Instant start, Instant end);

  List<WeeklySalesTotals> getWeeklySalesTotals(Instant start, Instant end);
}
