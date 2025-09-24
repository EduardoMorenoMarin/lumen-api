package com.librerialumen.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WeeklySalesTotals {

  LocalDate getWeekStart();

  LocalDate getWeekEnd();

  BigDecimal getTotalAmount();

  BigDecimal getTaxAmount();

  BigDecimal getDiscountAmount();
}
