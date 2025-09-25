package com.librerialumen.api.common;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class TestDateHelper {

  private TestDateHelper() {
  }

  public static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }

  public static Instant daysAgo(long days) {
    return now().minus(days, ChronoUnit.DAYS);
  }

  public static Instant daysFromNow(long days) {
    return now().plus(days, ChronoUnit.DAYS);
  }

  public static DateRange range(Instant start, Instant end) {
    return new DateRange(Objects.requireNonNull(start), Objects.requireNonNull(end));
  }

  public record DateRange(Instant start, Instant end) {
    public boolean contains(Instant value) {
      return !value.isBefore(start) && !value.isAfter(end);
    }
  }
}

