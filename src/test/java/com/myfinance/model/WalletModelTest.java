package com.myfinance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class WalletModelTest {

  @Test
  void applyTransactionShouldUpdateBalanceAndStoreTransactions() {
    Wallet wallet = new Wallet();

    LocalDateTime now = LocalDateTime.of(2025, 1, 1, 10, 0);

    Transaction income = new Income("i1", 20000.0, now, "Зарплата", "оклад");
    wallet.applyTransaction(income);

    Transaction expense = new Expense("e1", 3000.0, now.plusDays(1), "Еда", "продукты");
    wallet.applyTransaction(expense);

    // баланс = 20000 - 3000
    assertEquals(17000.0, wallet.getBalance(), 0.0001);
    // в кошельке две операции
    assertEquals(2, wallet.getTransactions().size());
  }

  @Test
  void totalIncomeTotalExpenseAndExpensesByCategoryShouldBeCorrect() {
    Wallet wallet = new Wallet();

    LocalDateTime d1 = LocalDateTime.of(2025, 5, 1, 10, 0);
    LocalDateTime d2 = LocalDateTime.of(2025, 5, 2, 11, 0);
    LocalDateTime d3 = LocalDateTime.of(2025, 5, 3, 12, 0);

    wallet.applyTransaction(new Income("i1", 10000.0, d1, "Зарплата", ""));
    wallet.applyTransaction(new Income("i2", 5000.0, d2, "Бонус", ""));
    wallet.applyTransaction(new Expense("e1", 2000.0, d2, "Еда", ""));
    wallet.applyTransaction(new Expense("e2", 1500.0, d3, "Еда", ""));
    wallet.applyTransaction(new Expense("e3", 1000.0, d3, "Такси", ""));

    double totalIncome = wallet.getTotalIncome();
    double totalExpense = wallet.getTotalExpense();
    double foodExpense = wallet.getExpensesByCategory("Еда");
    double taxiExpense = wallet.getExpensesByCategory("Такси");

    assertEquals(15000.0, totalIncome, 0.0001);
    assertEquals(4500.0, totalExpense, 0.0001);
    assertEquals(3500.0, foodExpense, 0.0001);
    assertEquals(1000.0, taxiExpense, 0.0001);
  }
}
