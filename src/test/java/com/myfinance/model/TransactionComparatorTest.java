package com.myfinance.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransactionComparatorTest {

  @Test
  void dateComparatorShouldSortByDateAscending() {
    LocalDateTime d1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    LocalDateTime d2 = LocalDateTime.of(2025, 1, 2, 10, 0);
    LocalDateTime d3 = LocalDateTime.of(2025, 1, 3, 10, 0);

    Transaction t1 = new Income("i1", 1000.0, d2, "Зарплата", "");
    Transaction t2 = new Expense("e1", 500.0, d3, "Еда", "");
    Transaction t3 = new Expense("e2", 200.0, d1, "Такси", "");

    List<Transaction> list = new ArrayList<>();
    list.add(t1);
    list.add(t2);
    list.add(t3);

    Collections.sort(list, new Transaction.DateComparator());

    // после сортировки по дате первым должен быть d1, потом d2, затем d3
    assertEquals(d1, list.get(0).getDateTime());
    assertEquals(d2, list.get(1).getDateTime());
    assertEquals(d3, list.get(2).getDateTime());
  }

  @Test
  void amountComparatorShouldSortByAmountAscending() {
    LocalDateTime now = LocalDateTime.of(2025, 1, 1, 10, 0);

    Transaction t1 = new Income("i1", 5000.0, now, "Зарплата", "");
    Transaction t2 = new Expense("e1", 200.0, now, "Еда", "");
    Transaction t3 = new Expense("e2", 1000.0, now, "Такси", "");

    List<Transaction> list = new ArrayList<>();
    list.add(t1);
    list.add(t2);
    list.add(t3);

    Collections.sort(list, new Transaction.AmountComparator());

    // после сортировки по сумме первым должен быть 200, затем 1000, затем 5000
    assertEquals(200.0, list.get(0).getAmount(), 0.0001);
    assertEquals(1000.0, list.get(1).getAmount(), 0.0001);
    assertEquals(5000.0, list.get(2).getAmount(), 0.0001);
  }
}
