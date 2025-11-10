package com.myfinance.cli;

import com.myfinance.model.Budget;
import com.myfinance.model.Transaction;
import com.myfinance.model.Wallet;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public final class TablePrinter {

  private static final DateTimeFormatter DATE_TIME_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private TablePrinter() {}

  public static void printBudgetTable(Map<String, Budget> budgets, Wallet wallet) {
    if (budgets.isEmpty()) {
      System.out.println("Бюджеты не заданы.");
      return;
    }
    System.out.printf("%-20s | %-12s | %-15s%n", "Категория", "Лимит", "Оставшийся бюджет");
    System.out.println("---------------------+--------------+-----------------");
    for (Budget budget : budgets.values()) {
      double spent = wallet.getExpensesByCategory(budget.getCategoryName());
      double remaining = budget.getLimit() - spent;
      System.out.printf(
          "%-20s | %-12.2f | %-15.2f%n", budget.getCategoryName(), budget.getLimit(), remaining);
    }
  }

  public static void printCategoryTotals(Map<String, Double> totals) {
    if (totals.isEmpty()) {
      System.out.println("Нет данных по категориям.");
      return;
    }
    System.out.printf("%-20s | %-12s%n", "Категория", "Сумма");
    System.out.println("---------------------+--------------");
    totals.forEach(
        (cat, sum) -> {
          System.out.printf("%-20s | %-12.2f%n", cat, sum);
        });
  }

  public static void printTransactionsTable(List<Transaction> txs) {
    if (txs.isEmpty()) {
      System.out.println("Операций нет.");
      return;
    }
    System.out.printf(
        "%-16s | %-16s | %-10s | %-15s | %-8s | %-20s%n",
        "ID", "Дата/время", "Сумма", "Категория", "Тип", "Описание");
    System.out.println(
        "-----------------+------------------+------------+-----------------+----------+----------------------");

    for (Transaction tx : txs) {
      String type = tx.isIncome() ? "Доход" : "Расход";
      String shortId = tx.getId().length() > 8 ? tx.getId().substring(0, 8) : tx.getId();
      System.out.printf(
          "%-16s | %-16s | %-10.2f | %-15s | %-8s | %-20s%n",
          shortId,
          tx.getDateTime().format(DATE_TIME_FMT),
          tx.getAmount(),
          tx.getCategoryName(),
          type,
          truncate(tx.getDescription(), 20));
    }
  }

  private static String truncate(String text, int maxLen) {
    if (text == null) {
      return "";
    }
    if (text.length() <= maxLen) {
      return text;
    }
    return text.substring(0, maxLen - 3) + "...";
  }
}
