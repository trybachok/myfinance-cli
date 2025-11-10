package com.myfinance.cli;

import com.myfinance.model.Budget;
import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import com.myfinance.model.Wallet;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

class HelpAndTablePrinterTest {

  @Test
  void helpPrinterShouldNotThrow() {
    HelpPrinter.printAuthHelp();
    HelpPrinter.printUserHelp();
  }

  @Test
  void tablePrinterShouldHandleEmptyAndFilledData() {
    // категории
    Map<String, Double> totals = new LinkedHashMap<>();
    TablePrinter.printCategoryTotals(totals); // пусто
    totals.put("Еда", 1000.0);
    totals.put("Развлечения", 2000.0);
    TablePrinter.printCategoryTotals(totals);

    // бюджеты
    Wallet wallet = new Wallet();
    TablePrinter.printBudgetTable(wallet.getBudgets(), wallet); // пусто

    wallet.getBudgets().put("еда", new Budget("Еда", 4000.0));
    wallet.applyTransaction(new Expense("e1", 500.0, LocalDateTime.now(), "Еда", "обед"));
    TablePrinter.printBudgetTable(wallet.getBudgets(), wallet);

    // транзакции
    List<Transaction> txs = new ArrayList<>();
    TablePrinter.printTransactionsTable(txs); // пустой список

    txs.add(new Income("i1", 20000.0, LocalDateTime.now(), "Зарплата", "оклад"));
    txs.add(new Expense("e2", 1000.0, LocalDateTime.now(), "Еда", "ужин"));
    TablePrinter.printTransactionsTable(txs);
  }
}
