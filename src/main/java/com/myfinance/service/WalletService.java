package com.myfinance.service;

import com.myfinance.model.Budget;
import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import com.myfinance.model.Wallet;
import com.myfinance.repository.WalletRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class WalletService {

  private final WalletRepository walletRepository;

  public WalletService(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }

  public Wallet loadWallet(String login) {
    return walletRepository.findByLogin(login).orElseGet(Wallet::new);
  }

  public void saveWallet(String login, Wallet wallet) {
    walletRepository.save(login, wallet);
  }

  public void addIncome(Wallet wallet, double amount, String categoryName, String description) {
    Income income =
        new Income(
            UUID.randomUUID().toString(), amount, LocalDateTime.now(), categoryName, description);
    wallet.applyTransaction(income);
    Wallet.Alert alert = wallet.new Alert();
    alert.checkBalance();
  }

  public void addExpense(Wallet wallet, double amount, String categoryName, String description) {
    Expense expense =
        new Expense(
            UUID.randomUUID().toString(), amount, LocalDateTime.now(), categoryName, description);
    wallet.applyTransaction(expense);
    Wallet.Alert alert = wallet.new Alert();
    alert.checkBalance();
    alert.checkCategoryLimit(categoryName);
  }

  public void setBudget(Wallet wallet, String categoryName, double limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Лимит бюджета должен быть > 0.");
    }
    String key = normalizeCategoryKey(categoryName);
    Map<String, Budget> budgets = wallet.getBudgets();
    Budget existing = budgets.get(key);
    if (existing == null) {
      budgets.put(key, new Budget(categoryName, limit));
    } else {
      existing.setLimit(limit);
      existing.setCategoryName(categoryName);
    }
  }

  public void removeBudget(Wallet wallet, String categoryName) {
    String key = normalizeCategoryKey(categoryName);
    wallet.getBudgets().remove(key);
  }

  private String normalizeCategoryKey(String categoryName) {
    if (categoryName == null) {
      return "";
    }
    return categoryName.trim().toLowerCase(Locale.ROOT);
  }

  public int importTransactions(Wallet wallet, Collection<Transaction> imported) {
    if (imported == null || imported.isEmpty()) {
      return 0;
    }
    int added = 0;
    for (Transaction tx : imported) {
      if (tx == null) {
        continue;
      }
      // пропускаем дубликаты по id, чтобы не задвоить операции
      if (wallet.getTransactions().containsKey(tx.getId())) {
        continue;
      }
      wallet.applyTransaction(tx);
      Wallet.Alert alert = wallet.new Alert();
      alert.checkBalance();
      if (!tx.isIncome()) {
        alert.checkCategoryLimit(tx.getCategoryName());
      }
      added++;
    }
    return added;
  }
}
