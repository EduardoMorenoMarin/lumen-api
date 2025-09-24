package com.librerialumen.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesTotals {

  LocalDate getDay();

  BigDecimal getTotalAmount();

  BigDecimal getTaxAmount();

  BigDecimal getDiscountAmount();
}
