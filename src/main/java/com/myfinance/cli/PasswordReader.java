package com.myfinance.cli;

import java.io.Console;
import java.util.Scanner;

public final class PasswordReader {

  private PasswordReader() {}

  /**
   * Считывает пароль без эха, если доступен System.console(). Если приложение запущено из IDE и
   * console == null, используем обычный Scanner (пароль будет виден).
   */
  public static String readPassword(String prompt, Scanner fallbackScanner) {
    Console console = System.console();
    if (console != null) {
      char[] passwordChars = console.readPassword("%s", prompt);
      if (passwordChars == null) {
        return "";
      }
      return new String(passwordChars);
    } else {
      System.out.print(prompt);
      return fallbackScanner.nextLine();
    }
  }
}
