package com.myfinance.cli;

public final class HelpPrinter {

  private HelpPrinter() {}

  public static void printAuthHelp() {
    System.out.println();
    System.out.println("=== Помощь по авторизации ===");
    System.out.println("1 - Войти: введите логин и пароль существующего пользователя.");
    System.out.println(
        "    Если логин не найден, приложение предложит сразу зарегистрировать его.");
    System.out.println(
        "2 - Зарегистрироваться: введите уникальный логин и пароль (от 6 символов).");
    System.out.println("3 - Сбросить пароль: введите логин и новый пароль.");
    System.out.println("0 - Выход из приложения.");
    System.out.println("Пример:");
    System.out.println("  > 2");
    System.out.println("  Логин: user1");
    System.out.println("  Пароль: mySecret");
  }

  public static void printUserHelp() {
    System.out.println();
    System.out.println("=== Помощь по функциям кошелька ===");
    System.out.println("1 - Добавить доход:");
    System.out.println("    Пример: категория 'Зарплата', сумма 20000, описание 'оклад'.");
    System.out.println("2 - Добавить расход:");
    System.out.println("    Пример: категория 'Еда', сумма 500, описание 'обед'.");
    System.out.println("3 - Управление бюджетами:");
    System.out.println("    - Установка лимита по категории (например, 'Еда' 4000).");
    System.out.println("    - Удаление бюджета.");
    System.out.println("    - Просмотр остатков по лимитам.");
    System.out.println("4 - Отчёты и статистика:");
    System.out.println("    - Общий доход, расходы, по категориям.");
    System.out.println(
        "    - Отчёт по периоду (например, 2025-01-01..2025-01-31) и выбранным категориям.");
    System.out.println("5 - Перевод другому пользователю:");
    System.out.println("    Нужно указать логин получателя, сумму и описание.");
    System.out.println("6 - Экспорт/импорт отчётов:");
    System.out.println("    - Экспорт: сохранение операций в CSV или JSON.");
    System.out.println("    - Импорт: чтение отчёта из файла и вывод статистики по нему.");
    System.out.println("0 - Выход: кошелёк будет сохранён в файл и вы вернётесь в главное меню.");
    System.out.println("help - Показать это меню.");
  }
}
