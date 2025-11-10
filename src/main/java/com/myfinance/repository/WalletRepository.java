package com.myfinance.repository;

import com.myfinance.model.Wallet;
import java.util.Optional;

public interface WalletRepository {

  Optional<Wallet> findByLogin(String login);

  void save(String login, Wallet wallet);
}
