package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService = new AuthService(new InMemoryUserRepository());
  }

  @Test
  void registerShouldCreateUser() {
    User user = authService.register("user1", "secret1");
    assertEquals("user1", user.getLogin());
    assertNotNull(user.getPasswordHash());
  }

  @Test
  void registerShouldFailOnShortPassword() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> authService.register("user1", "123"));
    assertTrue(ex.getMessage().contains("Пароль"));
  }

  @Test
  void registerShouldFailOnDuplicateLogin() {
    authService.register("user1", "secret1");
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class, () -> authService.register("user1", "another"));
    assertTrue(ex.getMessage().contains("Логин"));
  }

  @Test
  void loginShouldSucceedWithCorrectPassword() {
    authService.register("user1", "secret1");
    User user = authService.login("user1", "secret1");
    assertEquals("user1", user.getLogin());
  }

  @Test
  void loginShouldFailWithWrongPassword() {
    authService.register("user1", "secret1");
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> authService.login("user1", "wrong"));
    assertTrue(ex.getMessage().contains("Неверный пароль"));
  }

  @Test
  void resetPasswordShouldUpdatePasswordHash() {
    authService.register("user1", "secret1");
    authService.resetPassword("user1", "newSecret");

    // старый пароль не подходит
    assertThrows(IllegalArgumentException.class, () -> authService.login("user1", "secret1"));

    // новый подходит
    User user = authService.login("user1", "newSecret");
    assertEquals("user1", user.getLogin());
  }
}
