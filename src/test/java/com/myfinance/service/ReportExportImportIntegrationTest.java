package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportExportImportIntegrationTest {

  @TempDir Path tempDir;

  @Test
  void exportAndImportCsvShouldPreserveData() {
    ReportGenerator generator = new ReportGenerator();

    List<Transaction> original = new ArrayList<>();
    original.add(
        new Income("i1", 1000.0, LocalDateTime.of(2025, 1, 1, 10, 0), "Зарплата", "оклад"));
    original.add(new Expense("e1", 200.0, LocalDateTime.of(2025, 1, 2, 12, 0), "Еда", "обед"));

    Path csvFile = tempDir.resolve("report.csv");
    generator.exportTransactions(original, csvFile, ReportGenerator.ReportFormat.CSV);

    List<Transaction> imported =
        generator.importTransactions(csvFile, ReportGenerator.ReportFormat.CSV);

    assertEquals(2, imported.size());
    assertEquals(1000.0, imported.get(0).getAmount(), 0.0001);
    assertEquals("Зарплата", imported.get(0).getCategoryName());
    assertFalse(imported.get(1).isIncome());
    assertEquals("Еда", imported.get(1).getCategoryName());
  }

  @Test
  void exportAndImportJsonShouldPreserveData() {
    ReportGenerator generator = new ReportGenerator();

    List<Transaction> original = new ArrayList<>();
    original.add(new Income("i2", 3000.0, LocalDateTime.of(2025, 2, 1, 9, 30), "Бонус", "премия"));
    original.add(new Expense("e2", 500.0, LocalDateTime.of(2025, 2, 3, 18, 0), "Такси", "поездка"));

    Path jsonFile = tempDir.resolve("report.json");
    generator.exportTransactions(original, jsonFile, ReportGenerator.ReportFormat.JSON);

    List<Transaction> imported =
        generator.importTransactions(jsonFile, ReportGenerator.ReportFormat.JSON);

    assertEquals(2, imported.size());
    assertEquals(3000.0, imported.get(0).getAmount(), 0.0001);
    assertTrue(imported.get(0).isIncome());
    assertEquals("Бонус", imported.get(0).getCategoryName());

    assertEquals(500.0, imported.get(1).getAmount(), 0.0001);
    assertFalse(imported.get(1).isIncome());
    assertEquals("Такси", imported.get(1).getCategoryName());
  }
}
