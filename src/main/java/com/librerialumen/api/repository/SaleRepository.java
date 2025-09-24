package com.librerialumen.api.repository;

import com.librerialumen.api.domain.model.Sale;
import com.librerialumen.api.repository.projection.DailySalesTotals;
import com.librerialumen.api.repository.projection.WeeklySalesTotals;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

  @Query(value = """
      SELECT DATE(s.created_at) AS day,
             COALESCE(SUM(s.total_amount), 0)    AS totalAmount,
             COALESCE(SUM(s.tax_amount), 0)      AS taxAmount,
             COALESCE(SUM(s.discount_amount), 0) AS discountAmount
      FROM sales s
      WHERE s.created_at BETWEEN :start AND :end
      GROUP BY DATE(s.created_at)
      ORDER BY day
      """, nativeQuery = true)
  List<DailySalesTotals> findDailyTotals(@Param("start") Instant start,
                                         @Param("end") Instant end);

  @Query(value = """
      SELECT DATE(DATE_SUB(s.created_at, INTERVAL WEEKDAY(s.created_at) DAY))                            AS weekStart,
             DATE(DATE_ADD(DATE_SUB(s.created_at, INTERVAL WEEKDAY(s.created_at) DAY), INTERVAL 6 DAY)) AS weekEnd,
             COALESCE(SUM(s.total_amount), 0)                                                          AS totalAmount,
             COALESCE(SUM(s.tax_amount), 0)                                                            AS taxAmount,
             COALESCE(SUM(s.discount_amount), 0)                                                       AS discountAmount
      FROM sales s
      WHERE s.created_at BETWEEN :start AND :end
      GROUP BY weekStart, weekEnd
      ORDER BY weekStart
      """, nativeQuery = true)
  List<WeeklySalesTotals> findWeeklyTotals(@Param("start") Instant start,
                                           @Param("end") Instant end);

  List<Sale> findByCreatedAtBetween(Instant start, Instant end);
}
