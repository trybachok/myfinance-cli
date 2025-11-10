package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferServiceTest {

  private InMemoryWalletRepository walletRepository;
  private TransferService transferService;

  @BeforeEach
  void setUp() {
    walletRepository = new InMemoryWalletRepository();
    transferService = new TransferService(walletRepository);

    Wallet from = new Wallet();
    Wallet to = new Wallet();
    from.setBalance(1000.0);

    walletRepository.save("alice", from);
    walletRepository.save("bob", to);
  }

  @Test
  void transferShouldMoveMoneyBetweenWallets() {
    transferService.transfer("alice", "bob", 300.0, "возврат долга");

    Wallet alice = walletRepository.findByLogin("alice").orElseThrow();
    Wallet bob = walletRepository.findByLogin("bob").orElseThrow();

    assertEquals(700.0, alice.getBalance(), 0.0001);
    assertEquals(300.0, bob.getBalance(), 0.0001);

    assertEquals(1, alice.getTransactions().size());
    assertEquals(1, bob.getTransactions().size());
  }

  @Test
  void transferShouldFailOnSameLogin() {
    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer("alice", "alice", 100.0, "сам себе"));
  }

  @Test
  void transferShouldFailOnNonPositiveAmount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> transferService.transfer("alice", "bob", 0.0, "некорректно"));
  }
}
