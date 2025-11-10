package com.myfinance.util;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvUtilTest {

  @TempDir Path tempDir;

  @Test
  void writeAndReadTransactionsCsvShouldPreserveData() {
    Path file = tempDir.resolve("report.csv");

    List<Transaction> original =
        List.of(
            new Income("i1", 1000.0, LocalDateTime.of(2025, 1, 1, 12, 0), "Зарплата", "январь"),
            new Expense("e1", 500.0, LocalDateTime.of(2025, 1, 2, 13, 0), "Еда", "обед"));

    CsvUtil.writeTransactionsCsv(original, file);
    List<Transaction> read = CsvUtil.readTransactionsCsv(file);

    assertEquals(2, read.size());
    assertEquals(original.get(0).getAmount(), read.get(0).getAmount(), 0.0001);
    assertEquals(original.get(1).getCategoryName(), read.get(1).getCategoryName());
    assertFalse(read.get(0).isIncome() == read.get(1).isIncome());
  }
}
