package com.myfinance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class WalletAlertTest {

  @Test
  void checkBalanceShouldHandleZeroPositiveAndNegative() {
    Wallet wallet = new Wallet();

    // положительный баланс
    wallet.setBalance(100.0);
    Wallet.Alert alert = wallet.new Alert();
    alert.checkBalance(); // просто прогоняем ветку без вывода

    // нулевой баланс
    wallet.setBalance(0.0);
    alert.checkBalance();

    // отрицательный баланс
    wallet.setBalance(-10.0);
    alert.checkBalance();
  }

  @Test
  void checkCategoryLimitShouldHandleDifferentThresholds() {
    Wallet wallet = new Wallet();

    // бюджет на Еда: 100
    Budget budget = new Budget("Еда", 100.0);
    wallet.getBudgets().put("еда", budget);

    // до 80% — 50
    wallet.applyTransaction(new Expense("e1", 50.0, LocalDateTime.now(), "Еда", "покупка"));
    Wallet.Alert alert = wallet.new Alert();
    alert.checkCategoryLimit("Еда");

    // 90% — 90
    wallet.applyTransaction(new Expense("e2", 40.0, LocalDateTime.now(), "Еда", "ещё покупка"));
    alert.checkCategoryLimit("еда");

    // перерасход — 120
    wallet.applyTransaction(new Expense("e3", 30.0, LocalDateTime.now(), "Еда", "перерасход"));
    alert.checkCategoryLimit("ЕДА");

    // заодно проверим метод подсчёта по категории
    double spent = wallet.getExpensesByCategory("еда");
    assertEquals(120.0, spent, 0.0001);
  }
}
