package com.myfinance.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class ConsoleAndPasswordReaderTest {

  @Test
  void passwordReaderShouldUseScannerFallback() {
    String input = "mySecretPassword\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    String password = PasswordReader.readPassword("Пароль: ", scanner);
    assertEquals("mySecretPassword", password);
  }

  @Test
  void consoleAppConstructorShouldNotThrow() {
    assertDoesNotThrow(ConsoleApp::new);
  }
}
