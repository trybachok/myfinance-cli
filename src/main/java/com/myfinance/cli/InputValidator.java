package com.myfinance.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class InputValidator {

  private InputValidator() {}

  public static String requireNonEmpty(String input, String fieldName) {
    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException(fieldName + " не может быть пустым.");
    }
    return input.trim();
  }

  public static double requirePositiveDouble(String input, String fieldName) {
    requireNonEmpty(input, fieldName);
    try {
      double value = Double.parseDouble(input);
      if (value <= 0) {
        throw new IllegalArgumentException(fieldName + " должно быть > 0.");
      }
      return value;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(fieldName + " должно быть числом.", e);
    }
  }

  public static LocalDate requireDate(String input, String fieldName) {
    requireNonEmpty(input, fieldName);
    try {
      return LocalDate.parse(input); // формат YYYY-MM-DD
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(fieldName + " должно быть датой в формате YYYY-MM-DD.", e);
    }
  }
}
