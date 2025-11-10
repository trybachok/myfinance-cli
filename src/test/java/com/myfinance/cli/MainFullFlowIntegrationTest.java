package com.myfinance.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MainFullFlowIntegrationTest {

  @Test
  void mainShouldRegisterLoginAddIncomeAndExpenseAndExit() {
    String login = "intuser_" + UUID.randomUUID();
    String password = "secret123";

    String ls = System.lineSeparator();

    String input =
        new StringBuilder()
            // регистрация
            .append("2")
            .append(ls) // главное меню: Зарегистрироваться
            .append(login)
            .append(ls) // логин
            .append(password)
            .append(ls) // пароль
            // вход
            .append("1")
            .append(ls) // главное меню: Войти
            .append(login)
            .append(ls) // логин
            .append(password)
            .append(ls) // пароль
            // добавить доход
            .append("1")
            .append(ls) // меню кошелька: Добавить доход
            .append("Зарплата")
            .append(ls) // категория дохода
            .append("10000")
            .append(ls) // сумма
            .append("оклад")
            .append(ls) // описание
            // добавить расход
            .append("2")
            .append(ls) // меню кошелька: Добавить расход
            .append("Еда")
            .append(ls) // категория расхода
            .append("500")
            .append(ls) // сумма
            .append("обед")
            .append(ls) // описание
            // выйти из меню пользователя
            .append("0")
            .append(ls) // меню кошелька: выйти в главное меню
            // выйти из приложения
            .append("0")
            .append(ls) // главное меню: выход
            .toString();

    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
      assertDoesNotThrow(() -> Main.main(new String[0]));
    } finally {
      System.setIn(originalIn);
    }
  }

  @Test
  void mainShouldHandleBudgetMenu() {
    String login = "budgetuser_" + UUID.randomUUID();
    String password = "secret123";

    String ls = System.lineSeparator();

    String input =
        new StringBuilder()
            // регистрация
            .append("2")
            .append(ls) // Зарегистрироваться
            .append(login)
            .append(ls) // логин
            .append(password)
            .append(ls) // пароль
            // вход
            .append("1")
            .append(ls) // Войти
            .append(login)
            .append(ls) // логин
            .append(password)
            .append(ls) // пароль
            // меню кошелька: Управление бюджетами
            .append("3")
            .append(ls) // "Управление бюджетами"
            // меню бюджетов: установить/изменить бюджет
            .append("1")
            .append(ls) // "Установить/изменить бюджет категории"
            .append("Еда")
            .append(ls) // категория
            .append("4000")
            .append(ls) // лимит
            // меню бюджетов снова: показать бюджеты
            .append("3")
            .append(ls) // "Показать бюджеты и остатки"
            // назад из меню бюджетов
            .append("0")
            .append(ls) // назад в меню кошелька
            // выйти из меню пользователя
            .append("0")
            .append(ls) // выйти в главное меню
            // выйти из приложения
            .append("0")
            .append(ls) // завершить программу
            .toString();

    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
      assertDoesNotThrow(() -> Main.main(new String[0]));
    } finally {
      System.setIn(originalIn);
    }
  }

  @Test
  void mainShouldHandleTransferBetweenTwoUsers() {
    String loginA = "userA_" + UUID.randomUUID();
    String loginB = "userB_" + UUID.randomUUID();
    String password = "secret123";

    String ls = System.lineSeparator();

    String input =
        new StringBuilder()
            // регистрация пользователя A
            .append("2")
            .append(ls) // Зарегистрироваться
            .append(loginA)
            .append(ls) // логин A
            .append(password)
            .append(ls) // пароль A
            // регистрация пользователя B
            .append("2")
            .append(ls) // Зарегистрироваться
            .append(loginB)
            .append(ls) // логин B
            .append(password)
            .append(ls) // пароль B
            // вход под A
            .append("1")
            .append(ls) // Войти
            .append(loginA)
            .append(ls) // логин A
            .append(password)
            .append(ls) // пароль A
            // добавить доход A, чтобы было с чего переводить
            .append("1")
            .append(ls) // меню кошелька: Добавить доход
            .append("Зарплата")
            .append(ls) // категория
            .append("10000")
            .append(ls) // сумма
            .append("оклад")
            .append(ls) // описание
            // перевод пользователю B
            .append("5")
            .append(ls) // меню кошелька: Перевод другому пользователю
            .append(loginB)
            .append(ls) // логин получателя
            .append("3000")
            .append(ls) // сумма перевода
            .append("перевод")
            .append(ls) // описание
            // выйти из меню пользователя
            .append("0")
            .append(ls) // выйти в главное меню
            // выйти из приложения
            .append("0")
            .append(ls) // завершить программу
            .toString();

    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
      assertDoesNotThrow(() -> Main.main(new String[0]));
    } finally {
      System.setIn(originalIn);
    }
  }
}
