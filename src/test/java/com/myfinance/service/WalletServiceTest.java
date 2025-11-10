package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Budget;
import com.myfinance.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletServiceTest {

  private InMemoryWalletRepository walletRepository;
  private WalletService walletService;

  @BeforeEach
  void setUp() {
    walletRepository = new InMemoryWalletRepository();
    walletService = new WalletService(walletRepository);
  }

  @Test
  void addIncomeShouldIncreaseBalance() {
    Wallet wallet = new Wallet();
    walletService.addIncome(wallet, 1000.0, "Зарплата", "оклад");
    assertEquals(1000.0, wallet.getBalance(), 0.0001);
    assertEquals(1, wallet.getTransactions().size());
  }

  @Test
  void addExpenseShouldDecreaseBalance() {
    Wallet wallet = new Wallet();
    walletService.addIncome(wallet, 1000.0, "Зарплата", "оклад");
    walletService.addExpense(wallet, 300.0, "Еда", "обед");
    assertEquals(700.0, wallet.getBalance(), 0.0001);
    assertEquals(2, wallet.getTransactions().size());
  }

  @Test
  void setBudgetShouldCreateOrUpdateBudget() {
    Wallet wallet = new Wallet();
    walletService.setBudget(wallet, "Еда", 4000.0);

    // Ищем бюджет по отображаемому имени категории (без учёта регистра)
    Budget budget =
        wallet.getBudgets().values().stream()
            .filter(b -> "Еда".equalsIgnoreCase(b.getCategoryName()))
            .findFirst()
            .orElse(null);

    assertNotNull(budget);
    assertEquals(4000.0, budget.getLimit(), 0.0001);

    // Обновляем бюджет
    walletService.setBudget(wallet, "Еда", 5000.0);

    Budget updated =
        wallet.getBudgets().values().stream()
            .filter(b -> "Еда".equalsIgnoreCase(b.getCategoryName()))
            .findFirst()
            .orElse(null);

    assertNotNull(updated);
    assertEquals(5000.0, updated.getLimit(), 0.0001);
  }

  @Test
  void setBudgetShouldFailOnNonPositiveLimit() {
    Wallet wallet = new Wallet();
    assertThrows(IllegalArgumentException.class, () -> walletService.setBudget(wallet, "Еда", 0.0));
  }
}
