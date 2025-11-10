package com.myfinance.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MainIntegrationTest {

  @Test
  void mainShouldAllowRegisterAndExitWithoutErrors() {
    String login = "cliuser_" + UUID.randomUUID();
    String password = "secret123";

    // Сценарий:
    // 2 -> Зарегистрироваться
    // login
    // password
    // 0 -> Выход из приложения
    String ls = System.lineSeparator();
    String input =
        "2"
            + ls
            + // главное меню: регистрация
            login
            + ls
            + // ввод логина
            password
            + ls
            + // ввод пароля
            "0"
            + ls // главное меню: выход
        ;

    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
      assertDoesNotThrow(() -> Main.main(new String[0]));
    } finally {
      System.setIn(originalIn);
    }
  }
}
