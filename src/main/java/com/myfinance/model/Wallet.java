package com.myfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet {

  private double balance;
  private Map<String, Transaction> transactions = new LinkedHashMap<>();
  private Map<String, Budget> budgets = new HashMap<>();

  public Wallet() {}

  public double getBalance() {
    return balance;
  }

  public Map<String, Transaction> getTransactions() {
    return transactions;
  }

  public Map<String, Budget> getBudgets() {
    return budgets;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public void setTransactions(Map<String, Transaction> transactions) {
    this.transactions = transactions;
  }

  public void setBudgets(Map<String, Budget> budgets) {
    this.budgets = budgets;
  }

  public void applyTransaction(Transaction tx) {
    if (tx.isIncome()) {
      balance += tx.getAmount();
    } else {
      balance -= tx.getAmount();
    }
    transactions.put(tx.getId(), tx);
  }

  public double getTotalIncome() {
    return transactions.values().stream()
        .filter(Transaction::isIncome)
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public double getTotalExpense() {
    return transactions.values().stream()
        .filter(tx -> !tx.isIncome())
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public double getExpensesByCategory(String categoryName) {
    return transactions.values().stream()
        .filter(tx -> !tx.isIncome())
        .filter(tx -> tx.getCategoryName().equalsIgnoreCase(categoryName))
        .mapToDouble(Transaction::getAmount)
        .sum();
  }

  public class Alert {

    public void checkCategoryLimit(String categoryName) {
      if (categoryName == null || categoryName.isBlank()) {
        return;
      }

      Budget budget =
          budgets.values().stream()
              .filter(
                  b ->
                      b.getCategoryName() != null
                          && b.getCategoryName().equalsIgnoreCase(categoryName))
              .findFirst()
              .orElse(null);

      if (budget == null) {
        return;
      }

      double spent = getExpensesByCategory(budget.getCategoryName());
      double limit = budget.getLimit();
      if (limit <= 0) {
        return;
      }

      double percent = spent / limit * 100.0;

      if (spent > limit) {
        System.out.printf(
            "⚠ Перерасход по категории '%s': лимит %.2f, потрачено %.2f%n",
            budget.getCategoryName(), limit, spent);
      } else if (percent >= 100.0) {
        System.out.printf(
            "⚠ Достигнут 100%% лимита по категории '%s'!%n", budget.getCategoryName());
      } else if (percent >= 80.0) {
        System.out.printf(
            "⚠ Потрачено %.0f%% лимита по категории '%s'%n", percent, budget.getCategoryName());
      }
    }

    public void checkBalance() {
      if (balance == 0.0) {
        System.out.println("⚠ Баланс кошелька равен нулю.");
      } else if (balance < 0.0) {
        System.out.println("⚠ Расходы превысили доходы. Баланс отрицательный!");
      }
    }
  }
}
