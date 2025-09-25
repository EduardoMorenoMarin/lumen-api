package com.librerialumen.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;

public interface DailySalesTotals {

  @Value("#{target.day_value}")
  LocalDate getDay();

  BigDecimal getTotalAmount();

  BigDecimal getTaxAmount();

  BigDecimal getDiscountAmount();
}
