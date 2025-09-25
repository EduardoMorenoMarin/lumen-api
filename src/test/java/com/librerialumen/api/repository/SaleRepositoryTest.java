package com.librerialumen.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.librerialumen.api.ActiveTestProfile;
import com.librerialumen.api.domain.enums.SaleStatus;
import com.librerialumen.api.domain.model.Sale;
import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.repository.projection.DailySalesTotals;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@ActiveTestProfile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SaleRepositoryTest {

  @Autowired
  private SaleRepository saleRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("findDailyTotals aggregates amounts per day within the range")
  void findDailyTotals_shouldAggregatePerDay() {
    User cashier = persistUser("cashier@lumen.test");

    persistSale(createSale(cashier, new BigDecimal("100.00"), new BigDecimal("18.00"), new BigDecimal("5.00"), Instant.parse("2025-01-10T09:15:00Z")), Instant.parse("2025-01-10T09:15:00Z"));
    persistSale(createSale(cashier, new BigDecimal("50.00"), new BigDecimal("9.00"), BigDecimal.ZERO, Instant.parse("2025-01-10T12:30:00Z")), Instant.parse("2025-01-10T12:30:00Z"));
    persistSale(createSale(cashier, new BigDecimal("80.00"), new BigDecimal("14.40"), new BigDecimal("3.00"), Instant.parse("2025-01-11T11:00:00Z")), Instant.parse("2025-01-11T11:00:00Z"));
    persistSale(createSale(cashier, new BigDecimal("10.00"), new BigDecimal("1.50"), BigDecimal.ZERO, Instant.parse("2025-02-01T08:00:00Z")), Instant.parse("2025-02-01T08:00:00Z"));

    Instant start = Instant.parse("2025-01-09T00:00:00Z");
    Instant end = Instant.parse("2025-01-12T00:00:00Z");

    List<DailySalesTotals> totals = saleRepository.findDailyTotals(start, end);

    assertEquals(2, totals.size());

    DailySalesTotals firstDay = totals.get(0);
    assertEquals(LocalDate.parse("2025-01-10"), firstDay.getDay());
    assertEquals(new BigDecimal("150.00"), firstDay.getTotalAmount());
    assertEquals(new BigDecimal("27.00"), firstDay.getTaxAmount());
    assertEquals(new BigDecimal("5.00"), firstDay.getDiscountAmount());

    DailySalesTotals secondDay = totals.get(1);
    assertEquals(LocalDate.parse("2025-01-11"), secondDay.getDay());
    assertEquals(new BigDecimal("80.00"), secondDay.getTotalAmount());
    assertEquals(new BigDecimal("14.40"), secondDay.getTaxAmount());
    assertEquals(new BigDecimal("3.00"), secondDay.getDiscountAmount());
  }

  private Sale createSale(User cashier, BigDecimal total, BigDecimal tax, BigDecimal discount, Instant saleDate) {
    Sale sale = new Sale();
    sale.setStatus(SaleStatus.COMPLETED);
    sale.setSaleDate(saleDate);
    sale.setTotalAmount(total);
    sale.setTaxAmount(tax);
    sale.setDiscountAmount(discount);
    sale.setCashier(cashier);
    return sale;
  }

  private void persistSale(Sale sale, Instant createdAt) {
    entityManager.persist(sale);
    entityManager.flush();
    entityManager.getEntityManager()
        .createNativeQuery("UPDATE sales SET created_at = ?, updated_at = ? WHERE id = ?")
        .setParameter(1, Timestamp.from(createdAt))
        .setParameter(2, Timestamp.from(createdAt))
        .setParameter(3, sale.getId())
        .executeUpdate();
  }

  private User persistUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPasswordHash("password");
    user.setFirstName("Jane");
    user.setLastName("Cashier");
    user.setRole("ADMIN");
    user.setActive(true);
    entityManager.persist(user);
    entityManager.flush();
    assertNotNull(user.getId());
    return user;
  }
}
