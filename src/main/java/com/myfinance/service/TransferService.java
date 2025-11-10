package com.myfinance.service;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Wallet;
import com.myfinance.repository.WalletRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransferService {

  private final WalletRepository walletRepository;

  public TransferService(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }

  public void transfer(String fromLogin, String toLogin, double amount, String description) {
    if (fromLogin == null || fromLogin.isBlank()) {
      throw new IllegalArgumentException("Логин отправителя не может быть пустым.");
    }
    if (toLogin == null || toLogin.isBlank()) {
      throw new IllegalArgumentException("Логин получателя не может быть пустым.");
    }
    if (fromLogin.equals(toLogin)) {
      throw new IllegalArgumentException("Нельзя перевести самому себе.");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Сумма перевода должна быть больше нуля.");
    }

    Wallet from =
        walletRepository
            .findByLogin(fromLogin)
            .orElseThrow(() -> new IllegalArgumentException("Кошелёк отправителя не найден."));
    Wallet to =
        walletRepository
            .findByLogin(toLogin)
            .orElseThrow(() -> new IllegalArgumentException("Кошелёк получателя не найден."));

    LocalDateTime now = LocalDateTime.now();

    Expense expense =
        new Expense(
            UUID.randomUUID().toString(),
            amount,
            now,
            "Перевод пользователю " + toLogin,
            description);
    Income income =
        new Income(
            UUID.randomUUID().toString(), amount, now, "Перевод от " + fromLogin, description);

    from.applyTransaction(expense);
    to.applyTransaction(income);

    from.new Alert().checkBalance();
    to.new Alert().checkBalance();

    walletRepository.save(fromLogin, from);
    walletRepository.save(toLogin, to);
  }
}
