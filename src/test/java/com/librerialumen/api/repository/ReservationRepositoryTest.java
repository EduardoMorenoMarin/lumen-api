package com.librerialumen.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.librerialumen.api.ActiveTestProfile;
import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.domain.model.Reservation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@ActiveTestProfile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationRepositoryTest {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("findExpiredReservations returns expired pending and reserved reservations")
  void findExpiredReservations_shouldReturnPendingAndReserved() {
    Customer customer = persistCustomer("12345678");

    persistReservation(customer,
        ReservationStatus.RESERVED,
        Instant.parse("2025-01-01T09:00:00Z"),
        Instant.parse("2025-01-02T09:00:00Z"),
        "RSV-1001");

    persistReservation(customer,
        ReservationStatus.PENDING,
        Instant.parse("2025-01-01T10:00:00Z"),
        Instant.parse("2025-01-02T08:59:00Z"),
        "RSV-1002");

    persistReservation(customer,
        ReservationStatus.COMPLETED,
        Instant.parse("2025-01-01T11:00:00Z"),
        Instant.parse("2025-01-02T07:00:00Z"),
        "RSV-1003");

    persistReservation(customer,
        ReservationStatus.RESERVED,
        Instant.parse("2025-01-01T12:00:00Z"),
        Instant.parse("2025-01-02T11:00:00Z"),
        "RSV-1004");

    Instant now = Instant.parse("2025-01-02T10:00:00Z");

    List<Reservation> expired = reservationRepository.findExpiredReservations(now);

    Set<String> returnedCodes = expired.stream().map(Reservation::getCode).collect(Collectors.toSet());

    assertEquals(2, expired.size());
    assertTrue(returnedCodes.containsAll(Set.of("RSV-1001", "RSV-1002")));
    assertTrue(expired.stream().allMatch(reservation ->
        reservation.getStatus() == ReservationStatus.RESERVED || reservation.getStatus() == ReservationStatus.PENDING));
  }

  private Customer persistCustomer(String dni) {
    Customer customer = new Customer();
    customer.setFirstName("Ana");
    customer.setLastName("Lumen");
    customer.setDni(dni);
    customer.setEmail(dni + "@lumen.test");
    customer.setPhone("555123456");
    entityManager.persist(customer);
    return customer;
  }

  private Reservation persistReservation(Customer customer,
                                          ReservationStatus status,
                                          Instant reservationDate,
                                          Instant expirationDate,
                                          String code) {
    Reservation reservation = new Reservation();
    reservation.setCustomer(customer);
    reservation.setStatus(status);
    reservation.setReservationDate(reservationDate);
    reservation.setExpirationDate(expirationDate);
    reservation.setCode(code);
    reservation.setTotalAmount(new BigDecimal("150.00"));
    entityManager.persist(reservation);
    return reservation;
  }
}
