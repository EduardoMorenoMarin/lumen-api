package com.librerialumen.api.repository;

import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.domain.model.Reservation;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

  List<Reservation> findByStatus(ReservationStatus status);

  List<Reservation> findByStatusAndCreatedAtBetween(ReservationStatus status, Instant start, Instant end);

  List<Reservation> findByCreatedAtBetween(Instant start, Instant end);

  @Query("SELECT r FROM Reservation r WHERE r.status IN (com.librerialumen.api.domain.enums.ReservationStatus.RESERVED, com.librerialumen.api.domain.enums.ReservationStatus.PENDING) AND r.expirationDate IS NOT NULL AND r.expirationDate < :now")
  List<Reservation> findExpiredReservations(@Param("now") Instant now);
}
