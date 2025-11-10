package com.myfinance.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InputValidatorTest {

  @Test
  void requireNonEmptyShouldReturnTrimmedValue() {
    String result = InputValidator.requireNonEmpty("  hello  ", "Поле");
    assertEquals("hello", result);
  }

  @Test
  void requireNonEmptyShouldThrowOnBlank() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> InputValidator.requireNonEmpty("   ", "Поле"));
    assertTrue(ex.getMessage().contains("Поле"));
    assertTrue(ex.getMessage().toLowerCase().contains("не может быть пустым"));
  }

  @Test
  void requirePositiveDoubleShouldParseValidNumber() {
    double value = InputValidator.requirePositiveDouble("  12.5  ", "Сумма");
    assertEquals(12.5, value, 0.0001);
  }

  @Test
  void requirePositiveDoubleShouldThrowOnZeroOrNegative() {
    IllegalArgumentException ex1 =
        assertThrows(
            IllegalArgumentException.class,
            () -> InputValidator.requirePositiveDouble("0", "Сумма"));
    assertTrue(ex1.getMessage().contains("Сумма"));

    IllegalArgumentException ex2 =
        assertThrows(
            IllegalArgumentException.class,
            () -> InputValidator.requirePositiveDouble("-1", "Сумма"));
    assertTrue(ex2.getMessage().contains("Сумма"));
  }

  @Test
  void requirePositiveDoubleShouldThrowOnNonNumeric() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> InputValidator.requirePositiveDouble("abc", "Сумма"));
    assertTrue(ex.getMessage().toLowerCase().contains("числом"));
  }

  @Test
  void requireDateShouldParseIsoDate() {
    LocalDate date = InputValidator.requireDate("2025-01-31", "Дата");
    assertEquals(LocalDate.of(2025, 1, 31), date);
  }

  @Test
  void requireDateShouldThrowOnInvalidFormat() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> InputValidator.requireDate("31-01-2025", "Дата"));
    assertTrue(ex.getMessage().contains("Дата"));
    assertTrue(ex.getMessage().contains("YYYY-MM-DD"));
  }

  @Test
  void requireDateShouldThrowOnBlank() {
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> InputValidator.requireDate("   ", "Дата"));
    assertTrue(ex.getMessage().contains("Дата"));
  }
}
