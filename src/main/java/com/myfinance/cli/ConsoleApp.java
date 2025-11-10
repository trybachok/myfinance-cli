package com.myfinance.cli;

import com.myfinance.model.Transaction;
import com.myfinance.model.User;
import com.myfinance.model.Wallet;
import com.myfinance.repository.UserRepository;
import com.myfinance.repository.WalletRepository;
import com.myfinance.repository.impl.FileUserRepository;
import com.myfinance.repository.impl.FileWalletRepository;
import com.myfinance.service.AuthService;
import com.myfinance.service.ReportGenerator;
import com.myfinance.service.ReportGenerator.ReportFormat;
import com.myfinance.service.TransferService;
import com.myfinance.service.WalletService;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class ConsoleApp {

  private final Scanner scanner = new Scanner(System.in);

  private final UserRepository userRepository;
  private final AuthService authService;
  private final WalletService walletService;
  private final TransferService transferService;
  private final ReportGenerator reportGenerator;

  public ConsoleApp() {
    this.userRepository = new FileUserRepository();
    WalletRepository walletRepository = new FileWalletRepository();
    this.authService = new AuthService(userRepository);
    this.walletService = new WalletService(walletRepository);
    this.transferService = new TransferService(walletRepository);
    this.reportGenerator = new ReportGenerator();
  }

  public void run() {
    System.out.println("=== Личный финансовый менеджер ===");
    while (true) {
      printWelcomeMenu();
      String cmd = scanner.nextLine().trim();
      switch (cmd) {
        case "1" -> handleLogin();
        case "2" -> handleRegister();
        case "3" -> handleResetPassword();
        case "help" -> HelpPrinter.printAuthHelp();
        case "0" -> {
          System.out.println("До свидания!");
          return;
        }
        default -> System.out.println("Неизвестная команда. Введите 'help' для помощи.");
      }
    }
  }

  private void printWelcomeMenu() {
    System.out.println();
    System.out.println("Главное меню:");
    System.out.println("1 - Войти");
    System.out.println("2 - Зарегистрироваться");
    System.out.println("3 - Сбросить пароль");
    System.out.println("help - Показать помощь");
    System.out.println("0 - Выход");
    System.out.print("Ваш выбор: ");
  }

  private void handleLogin() {
    System.out.print("Логин: ");
    String login = scanner.nextLine();

    login = InputValidator.requireNonEmpty(login, "Логин");

    if (userRepository.findByLogin(login).isEmpty()) {
      System.out.println("Пользователь с таким логином не найден.");
      System.out.println("1 - Зарегистрироваться с этим логином");
      System.out.println("2 - Ввести другой логин");
      System.out.println("0 - Назад");
      System.out.print("Ваш выбор: ");
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> handleRegisterWithLogin(login);
        case "2" -> handleLogin();
        default -> {}
      }
      return;
    }

    String password = PasswordReader.readPassword("Пароль: ", scanner);
    try {
      User user = authService.login(login, password);
      System.out.printf("Успешный вход. Привет, %s!%n", user.getLogin());
      Wallet wallet = walletService.loadWallet(user.getLogin());
      userMenu(user, wallet);
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void handleRegister() {
    System.out.print("Логин: ");
    String login = scanner.nextLine();
    String password = PasswordReader.readPassword("Пароль: ", scanner);

    try {
      User user = authService.register(login, password);
      System.out.printf("Пользователь '%s' успешно зарегистрирован.%n", user.getLogin());
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка регистрации: " + ex.getMessage());
    }
  }

  private void handleRegisterWithLogin(String login) {
    String password = PasswordReader.readPassword("Придумайте пароль: ", scanner);
    try {
      User user = authService.register(login, password);
      System.out.printf("Пользователь '%s' зарегистрирован.%n", user.getLogin());
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка регистрации: " + ex.getMessage());
    }
  }

  private void handleResetPassword() {
    System.out.print("Введите логин для сброса пароля: ");
    String login = scanner.nextLine();
    String newPassword = PasswordReader.readPassword("Введите новый пароль: ", scanner);
    try {
      authService.resetPassword(login, newPassword);
      System.out.println("Пароль успешно обновлён.");
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void userMenu(User user, Wallet wallet) {
    while (true) {
      System.out.println();
      System.out.printf("=== Кошелёк пользователя %s ===%n", user.getLogin());
      System.out.printf("Текущий баланс: %.2f%n", wallet.getBalance());
      System.out.println("1 - Добавить доход");
      System.out.println("2 - Добавить расход");
      System.out.println("3 - Управление бюджетами");
      System.out.println("4 - Отчёты и статистика");
      System.out.println("5 - Перевод другому пользователю");
      System.out.println("6 - Экспорт/импорт отчётов");
      System.out.println("help - Помощь по функциям");
      System.out.println("0 - Выйти (сохранить кошелёк и вернуться в главное меню)");
      System.out.print("Ваш выбор: ");

      String cmd = scanner.nextLine().trim();
      switch (cmd) {
        case "1" -> addIncomeFlow(wallet, user);
        case "2" -> addExpenseFlow(wallet, user);
        case "3" -> budgetsMenu(wallet, user);
        case "4" -> reportsMenu(wallet);
        case "5" -> transferFlow(user);
        case "6" -> exportImportMenu(wallet, user);
        case "help" -> HelpPrinter.printUserHelp();
        case "0" -> {
          walletService.saveWallet(user.getLogin(), wallet);
          System.out.println("Кошелёк сохранён. Возврат в главное меню.");
          return;
        }
        default -> System.out.println("Неизвестная команда. Введите 'help' для списка.");
      }
    }
  }

  private void addIncomeFlow(Wallet wallet, User user) {
    try {
      System.out.print("Категория дохода (например, Зарплата): ");
      String category = InputValidator.requireNonEmpty(scanner.nextLine(), "Категория");
      System.out.print("Сумма: ");
      double amount = InputValidator.requirePositiveDouble(scanner.nextLine(), "Сумма");
      System.out.print("Описание (необязательно): ");
      String description = scanner.nextLine();

      walletService.addIncome(wallet, amount, category, description);
      walletService.saveWallet(user.getLogin(), wallet);
      System.out.println("Доход успешно добавлен.");
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void addExpenseFlow(Wallet wallet, User user) {
    try {
      System.out.print("Категория расхода (например, Еда): ");
      String category = InputValidator.requireNonEmpty(scanner.nextLine(), "Категория");
      System.out.print("Сумма: ");
      double amount = InputValidator.requirePositiveDouble(scanner.nextLine(), "Сумма");
      System.out.print("Описание (необязательно): ");
      String description = scanner.nextLine();

      walletService.addExpense(wallet, amount, category, description);
      walletService.saveWallet(user.getLogin(), wallet);
      System.out.println("Расход успешно добавлен.");
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void budgetsMenu(Wallet wallet, User user) {
    while (true) {
      System.out.println();
      System.out.println("=== Управление бюджетами ===");
      System.out.println("1 - Установить/изменить бюджет категории");
      System.out.println("2 - Удалить бюджет категории");
      System.out.println("3 - Показать бюджеты и остатки");
      System.out.println("0 - Назад");
      System.out.print("Ваш выбор: ");

      String cmd = scanner.nextLine().trim();
      switch (cmd) {
        case "1" -> setBudgetFlow(wallet, user);
        case "2" -> removeBudgetFlow(wallet, user);
        case "3" -> TablePrinter.printBudgetTable(wallet.getBudgets(), wallet);
        case "0" -> {
          walletService.saveWallet(user.getLogin(), wallet);
          return;
        }
        default -> System.out.println("Неизвестная команда.");
      }
    }
  }

  private void setBudgetFlow(Wallet wallet, User user) {
    try {
      System.out.print("Категория: ");
      String category = InputValidator.requireNonEmpty(scanner.nextLine(), "Категория");
      System.out.print("Лимит бюджета: ");
      double limit = InputValidator.requirePositiveDouble(scanner.nextLine(), "Лимит");
      walletService.setBudget(wallet, category, limit);
      walletService.saveWallet(user.getLogin(), wallet);
      System.out.println("Бюджет установлен/обновлён.");
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void removeBudgetFlow(Wallet wallet, User user) {
    System.out.print("Категория для удаления бюджета: ");
    String category = scanner.nextLine();
    walletService.removeBudget(wallet, category);
    walletService.saveWallet(user.getLogin(), wallet);
    System.out.println("Бюджет (если существовал) удалён.");
  }

  private void reportsMenu(Wallet wallet) {
    while (true) {
      System.out.println();
      System.out.println("=== Отчёты и статистика ===");
      System.out.println("1 - Общий итог (доходы, расходы, по категориям)");
      System.out.println("2 - Отчёт по периоду и категориям");
      System.out.println("0 - Назад");
      System.out.print("Ваш выбор: ");

      String cmd = scanner.nextLine().trim();
      switch (cmd) {
        case "1" -> printSummaryReport(wallet);
        case "2" -> detailedReportFlow(wallet);
        case "0" -> {
          return;
        }
        default -> System.out.println("Неизвестная команда.");
      }
    }
  }

  private void printSummaryReport(Wallet wallet) {
    Collection<Transaction> txs = wallet.getTransactions().values();

    double totalIncome = reportGenerator.totalIncome(txs);
    double totalExpense = reportGenerator.totalExpense(txs);
    Map<String, Double> incomeByCat = reportGenerator.incomeByCategory(txs);
    Map<String, Double> expenseByCat = reportGenerator.expenseByCategory(txs);

    System.out.println();
    System.out.printf("Общий доход: %.2f%n", totalIncome);
    System.out.println("Доходы по категориям:");
    TablePrinter.printCategoryTotals(incomeByCat);

    System.out.printf("Общие расходы: %.2f%n", totalExpense);
    System.out.println("Расходы по категориям:");
    TablePrinter.printCategoryTotals(expenseByCat);

    System.out.println("Бюджеты и остатки:");
    TablePrinter.printBudgetTable(wallet.getBudgets(), wallet);
  }

  private void detailedReportFlow(Wallet wallet) {
    try {
      System.out.print("Дата начала (YYYY-MM-DD, пусто - без ограничения): ");
      String fromStr = scanner.nextLine().trim();
      LocalDate from =
          fromStr.isEmpty() ? null : InputValidator.requireDate(fromStr, "Дата начала");

      System.out.print("Дата окончания (YYYY-MM-DD, пусто - без ограничения): ");
      String toStr = scanner.nextLine().trim();
      LocalDate to = toStr.isEmpty() ? null : InputValidator.requireDate(toStr, "Дата окончания");

      System.out.print("Категории через запятую (пусто - все): ");
      String catsStr = scanner.nextLine().trim();
      Set<String> categories = new HashSet<>();
      if (!catsStr.isEmpty()) {
        String[] parts = catsStr.split(",");
        for (String p : parts) {
          String cat = p.trim();
          if (!cat.isEmpty()) {
            categories.add(cat);
          }
        }
      }

      List<Transaction> filtered =
          reportGenerator.filterByPeriodAndCategories(
              wallet.getTransactions().values(), from, to, categories);

      if (filtered.isEmpty()) {
        System.out.println("По заданным условиям операций не найдено.");
        return;
      }

      if (!categories.isEmpty()) {
        Set<String> existingCats = new HashSet<>();
        for (Transaction tx : wallet.getTransactions().values()) {
          existingCats.add(tx.getCategoryName());
        }
        for (String cat : categories) {
          if (!existingCats.contains(cat)) {
            System.out.printf("Внимание: категория '%s' не найдена ни в одной операции.%n", cat);
          }
        }
      }

      System.out.println("Отфильтрованные операции:");
      TablePrinter.printTransactionsTable(filtered);

      double totalIncome = reportGenerator.totalIncome(filtered);
      double totalExpense = reportGenerator.totalExpense(filtered);
      System.out.printf("Доходов в выборке: %.2f, расходов: %.2f%n", totalIncome, totalExpense);

    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка: " + ex.getMessage());
    }
  }

  private void transferFlow(User user) {
    try {
      System.out.print("Логин получателя: ");
      String toLogin = InputValidator.requireNonEmpty(scanner.nextLine(), "Логин получателя");
      System.out.print("Сумма перевода: ");
      double amount = InputValidator.requirePositiveDouble(scanner.nextLine(), "Сумма");
      System.out.print("Описание (необязательно): ");
      String description = scanner.nextLine();

      transferService.transfer(user.getLogin(), toLogin, amount, description);
      System.out.println("Перевод выполнен.");
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка перевода: " + ex.getMessage());
    }
  }

  private void exportImportMenu(Wallet wallet, User user) {
    while (true) {
      System.out.println();
      System.out.println("=== Экспорт / импорт отчётов ===");
      System.out.println("1 - Экспортировать все операции в файл");
      System.out.println(
          "2 - Импортировать операции из файла (для отчёта и/или добавления в кошелёк)");
      System.out.println("0 - Назад");
      System.out.print("Ваш выбор: ");

      String cmd = scanner.nextLine().trim();
      switch (cmd) {
        case "1" -> exportTransactionsFlow(wallet);
        case "2" -> importTransactionsFlow(wallet, user);
        case "0" -> {
          return;
        }
        default -> System.out.println("Неизвестная команда.");
      }
    }
  }

  private void exportTransactionsFlow(Wallet wallet) {
    try {
      ReportFormat format = askReportFormat();

      String extExample = (format == ReportFormat.CSV) ? "report.csv" : "report.json";
      System.out.print("Путь к файлу (например, src/db/" + extExample + "): ");
      String pathStr = InputValidator.requireNonEmpty(scanner.nextLine(), "Путь");

      String adjustedPathStr = adjustExtensionToFormat(pathStr, format);
      Path path = Path.of(adjustedPathStr);

      reportGenerator.exportTransactions(wallet.getTransactions().values(), path, format);
      System.out.println("Отчёт успешно экспортирован в файл: " + path);
    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка экспорта: " + ex.getMessage());
    }
  }

  private String adjustExtensionToFormat(String pathStr, ReportFormat format) {
    String desiredExt = (format == ReportFormat.CSV) ? ".csv" : ".json";
    String lower = pathStr.toLowerCase();

    if (lower.endsWith(".csv") || lower.endsWith(".json")) {
      int dotIndex = pathStr.lastIndexOf('.');
      return pathStr.substring(0, dotIndex) + desiredExt;
    }
    return pathStr + desiredExt;
  }

  private void importTransactionsFlow(Wallet wallet, User user) {
    try {
      System.out.print("Путь к файлу: ");
      String pathStr = InputValidator.requireNonEmpty(scanner.nextLine(), "Путь");
      Path path = Path.of(pathStr);

      ReportFormat format = askReportFormat();
      List<Transaction> txs = reportGenerator.importTransactions(path, format);
      if (txs.isEmpty()) {
        System.out.println("В файле не найдено операций.");
        return;
      }

      System.out.printf("Найдено операций в файле: %d%n", txs.size());
      System.out.println("Операции из файла:");
      TablePrinter.printTransactionsTable(txs);

      double totalIncome = reportGenerator.totalIncome(txs);
      double totalExpense = reportGenerator.totalExpense(txs);
      System.out.printf("Доходов в файле: %.2f, расходов: %.2f%n", totalIncome, totalExpense);

      System.out.println();
      System.out.print("Добавить эти операции в текущий кошелёк и сохранить? (y/n): ");
      String choice = scanner.nextLine().trim().toLowerCase();

      if (choice.equals("y") || choice.equals("yes") || choice.equals("д") || choice.equals("да")) {
        double oldBalance = wallet.getBalance();
        int added = walletService.importTransactions(wallet, txs);

        if (added == 0) {
          System.out.println("Все операции из файла уже есть в кошельке. Ничего не добавлено.");
          return;
        }

        walletService.saveWallet(user.getLogin(), wallet);
        System.out.printf("Добавлено операций: %d%n", added);
        System.out.printf("Баланс был: %.2f, стал: %.2f%n", oldBalance, wallet.getBalance());
      } else {
        System.out.println("Операции использованы только для отчёта. Кошелёк не изменён.");
      }

    } catch (IllegalArgumentException ex) {
      System.out.println("Ошибка импорта: " + ex.getMessage());
    }
  }

  private ReportFormat askReportFormat() {
    System.out.print("Формат (1 - CSV, 2 - JSON): ");
    String fmt = scanner.nextLine().trim();
    return switch (fmt) {
      case "1" -> ReportFormat.CSV;
      case "2" -> ReportFormat.JSON;
      default -> throw new IllegalArgumentException("Неизвестный формат (ожидается 1 или 2).");
    };
  }
}
