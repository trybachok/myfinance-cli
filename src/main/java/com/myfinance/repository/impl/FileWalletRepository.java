package com.myfinance.repository.impl;

import static com.myfinance.repository.impl.DbPaths.WALLETS_DIR;

import com.myfinance.model.Wallet;
import com.myfinance.repository.WalletRepository;
import com.myfinance.util.JsonFileUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileWalletRepository implements WalletRepository {

  public FileWalletRepository() {
    // DbPaths статик-блок уже всё создал
  }

  private Path walletPath(String login) {
    return WALLETS_DIR.resolve(login + ".json");
  }

  @Override
  public Optional<Wallet> findByLogin(String login) {
    Path path = walletPath(login);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    Wallet wallet = JsonFileUtil.readObject(path, Wallet.class, new Wallet());
    return Optional.of(wallet);
  }

  @Override
  public void save(String login, Wallet wallet) {
    Path path = walletPath(login);
    JsonFileUtil.writeObject(path, wallet);
  }
}
