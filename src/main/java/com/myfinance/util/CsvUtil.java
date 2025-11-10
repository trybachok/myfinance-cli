package com.myfinance.util;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {

  private CsvUtil() {}

  // Формат CSV:
  // id;datetime;amount;category;type;description
  // type: INCOME или EXPENSE
  public static void writeTransactionsCsv(List<Transaction> txs, Path path) {
    List<String> lines = new ArrayList<>();
    lines.add("id;datetime;amount;category;type;description");
    for (Transaction tx : txs) {
      String type = tx.isIncome() ? "INCOME" : "EXPENSE";
      String line =
          String.join(
              ";",
              escape(tx.getId()),
              escape(tx.getDateTime().toString()),
              String.valueOf(tx.getAmount()),
              escape(tx.getCategoryName()),
              type,
              escape(tx.getDescription() == null ? "" : tx.getDescription()));
      lines.add(line);
    }

    try {
      Files.write(path, lines);
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка записи CSV: " + path, e);
    }
  }

  public static List<Transaction> readTransactionsCsv(Path path) {
    List<Transaction> result = new ArrayList<>();
    if (!Files.exists(path)) {
      throw new IllegalArgumentException("Файл не найден: " + path);
    }
    try {
      List<String> lines = Files.readAllLines(path);
      if (lines.isEmpty()) {
        throw new IllegalArgumentException("Файл пуст: " + path);
      }

      // проверяем заголовок
      String header = lines.get(0).trim();
      if (!header.equalsIgnoreCase("id;datetime;amount;category;type;description")) {
        throw new IllegalArgumentException(
            "Некорректный заголовок CSV. Ожидается: id;datetime;amount;category;type;description");
      }

      // строки начинаются со второй (index 1)
      for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i).trim();
        if (line.isEmpty()) {
          continue;
        }

        String[] parts = line.split(";", -1);
        int lineNumber = i + 1; // для человека

        if (parts.length < 5) {
          throw new IllegalArgumentException(
              "Ошибка в файле "
                  + path
                  + ", строка "
                  + lineNumber
                  + ": ожидается не менее 5 столбцов (id;datetime;amount;category;type[;description]).");
        }

        String id = unescape(parts[0]).trim();
        String dtStr = unescape(parts[1]).trim();
        String amountStr = parts[2].trim();
        String category = unescape(parts[3]).trim();
        String type = parts[4].trim();
        String description = parts.length > 5 ? unescape(parts[5]) : "";

        if (id.isEmpty()) {
          throw new IllegalArgumentException(
              "Ошибка в файле " + path + ", строка " + lineNumber + ": поле 'id' обязательно.");
        }

        LocalDateTime dt;
        try {
          dt = LocalDateTime.parse(dtStr);
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException(
              "Ошибка в файле "
                  + path
                  + ", строка "
                  + lineNumber
                  + ": неверный формат даты/времени '"
                  + dtStr
                  + "'. Ожидается ISO-формат, например 2025-01-01T12:00:00",
              e);
        }

        double amount;
        try {
          amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Ошибка в файле "
                  + path
                  + ", строка "
                  + lineNumber
                  + ": сумма должна быть числом, получено '"
                  + amountStr
                  + "'",
              e);
        }

        if (amount <= 0) {
          throw new IllegalArgumentException(
              "Ошибка в файле " + path + ", строка " + lineNumber + ": сумма должна быть > 0.");
        }

        if (category.isEmpty()) {
          throw new IllegalArgumentException(
              "Ошибка в файле "
                  + path
                  + ", строка "
                  + lineNumber
                  + ": поле 'category' обязательно.");
        }

        Transaction tx;
        if ("INCOME".equalsIgnoreCase(type)) {
          tx = new Income(id, amount, dt, category, description);
        } else if ("EXPENSE".equalsIgnoreCase(type)) {
          tx = new Expense(id, amount, dt, category, description);
        } else {
          throw new IllegalArgumentException(
              "Ошибка в файле "
                  + path
                  + ", строка "
                  + lineNumber
                  + ": поле 'type' должно быть INCOME или EXPENSE, получено '"
                  + type
                  + "'.");
        }

        result.add(tx);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка чтения CSV: " + path, e);
    }
    return result;
  }

  private static String escape(String value) {
    if (value == null) {
      return "";
    }
    return value.replace(";", "\\;").replace("\n", "\\n");
  }

  private static String unescape(String value) {
    if (value == null) {
      return "";
    }
    return value.replace("\\n", "\n").replace("\\;", ";");
  }
}
