package com.librerialumen.api.scheduler;

import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.domain.model.Reservation;
import com.librerialumen.api.repository.ReservationRepository;
import com.librerialumen.api.service.AuditService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

  private final ReservationRepository reservationRepository;
  private final AuditService auditService;

  @Scheduled(cron = "0 */5 * * * *")
  @Transactional
  public void markExpiredReservations() {
    Instant now = Instant.now();
    List<Reservation> expired = reservationRepository.findExpiredReservations(now);
    if (expired.isEmpty()) {
      return;
    }

    expired.forEach(reservation -> {
      ReservationStatus previousStatus = reservation.getStatus();
      reservation.setStatus(ReservationStatus.EXPIRED);
      auditService.record(
          "Reservation",
          reservation.getId().toString(),
          "MARK_EXPIRED",
          null,
          Map.of(
              "previousStatus", previousStatus.name(),
              "expiredAt", now.toString()
          ));
    });

    reservationRepository.saveAll(expired);
    log.info("Marked {} reservations as EXPIRED", expired.size());
  }
}
