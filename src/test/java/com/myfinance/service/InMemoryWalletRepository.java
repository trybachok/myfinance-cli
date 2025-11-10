package com.myfinance.service;

import com.myfinance.model.Wallet;
import com.myfinance.repository.WalletRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWalletRepository implements WalletRepository {

  private final Map<String, Wallet> wallets = new HashMap<>();

  @Override
  public Optional<Wallet> findByLogin(String login) {
    return Optional.ofNullable(wallets.get(login));
  }

  @Override
  public void save(String login, Wallet wallet) {
    wallets.put(login, wallet);
  }
}
