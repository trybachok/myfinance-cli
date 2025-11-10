package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReportGeneratorTest {

  private final ReportGenerator generator = new ReportGenerator();

  private List<Transaction> sampleTransactions() {
    LocalDateTime d1 = LocalDateTime.of(2025, 5, 1, 10, 0);
    LocalDateTime d2 = LocalDateTime.of(2025, 5, 2, 10, 0);
    LocalDateTime d3 = LocalDateTime.of(2025, 5, 3, 10, 0);

    return List.of(
        new Income("i1", 20000, d1, "Зарплата", ""),
        new Income("i2", 40000, d2, "Зарплата", ""),
        new Income("i3", 3000, d3, "Бонус", ""),
        new Expense("e1", 300, d1, "Еда", ""),
        new Expense("e2", 500, d2, "Еда", ""),
        new Expense("e3", 3000, d2, "Развлечения", ""),
        new Expense("e4", 3000, d3, "Коммунальные услуги", ""),
        new Expense("e5", 1500, d3, "Такси", ""));
  }

  @Test
  void totalsShouldMatchExampleFromTask() {
    List<Transaction> txs = sampleTransactions();
    double totalIncome = generator.totalIncome(txs);
    double totalExpense = generator.totalExpense(txs);

    assertEquals(63000.0, totalIncome, 0.0001);
    assertEquals(8300.0, totalExpense, 0.0001);
  }

  @Test
  void incomeByCategoryShouldGroupCorrectly() {
    Map<String, Double> map = generator.incomeByCategory(sampleTransactions());
    assertEquals(60000.0, map.get("Зарплата"), 0.0001);
    assertEquals(3000.0, map.get("Бонус"), 0.0001);
  }

  @Test
  void filterByPeriodShouldLimitDates() {
    List<Transaction> txs = sampleTransactions();
    LocalDate from = LocalDate.of(2025, 5, 2);
    LocalDate to = LocalDate.of(2025, 5, 2);

    List<Transaction> filtered =
        generator.filterByPeriodAndCategories(txs, from, to, Collections.emptySet());

    // в примере на 2-е число у нас i2 и e2 и e3
    assertEquals(3, filtered.size());
    assertTrue(
        filtered.stream()
            .allMatch(tx -> tx.getDateTime().toLocalDate().equals(LocalDate.of(2025, 5, 2))));
  }

  @Test
  void filterByCategoriesShouldLimitCategories() {
    List<Transaction> txs = sampleTransactions();
    Set<String> categories = Set.of("Еда");

    List<Transaction> filtered = generator.filterByPeriodAndCategories(txs, null, null, categories);

    assertEquals(2, filtered.size());
    assertTrue(filtered.stream().allMatch(tx -> tx.getCategoryName().equals("Еда")));
  }

  @Test
  void sortByAmountDescShouldSortCorrectly() {
    List<Transaction> txs = new ArrayList<>(sampleTransactions());
    generator.sortByAmountDesc(txs);
    assertTrue(txs.get(0).getAmount() >= txs.get(1).getAmount());
    assertEquals(40000.0, txs.get(0).getAmount(), 0.0001); // самый большой доход
  }
}
