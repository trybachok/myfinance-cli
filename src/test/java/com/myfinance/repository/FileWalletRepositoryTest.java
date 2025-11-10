// src/test/java/com/myfinance/repository/FileWalletRepositoryTest.java
package com.myfinance.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Wallet;
import com.myfinance.repository.impl.FileWalletRepository;
import org.junit.jupiter.api.Test;

class FileWalletRepositoryTest {

  @Test
  void saveAndLoadWallet() {
    FileWalletRepository repo = new FileWalletRepository();
    Wallet wallet = new Wallet();
    wallet.setBalance(123.45);

    repo.save("walletTestUser", wallet);
    Wallet loaded = repo.findByLogin("walletTestUser").orElseThrow();

    assertEquals(123.45, loaded.getBalance(), 0.0001);
  }
}
