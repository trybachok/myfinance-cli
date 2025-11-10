package com.myfinance.service;

import com.myfinance.model.Expense;
import com.myfinance.model.Income;
import com.myfinance.model.Transaction;
import com.myfinance.util.CsvUtil;
import com.myfinance.util.JsonFileUtil;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

  public double totalIncome(Collection<Transaction> txs) {
    return txs.stream().filter(Transaction::isIncome).mapToDouble(Transaction::getAmount).sum();
  }

  public double totalExpense(Collection<Transaction> txs) {
    return txs.stream().filter(tx -> !tx.isIncome()).mapToDouble(Transaction::getAmount).sum();
  }

  public Map<String, Double> incomeByCategory(Collection<Transaction> txs) {
    Map<String, Double> result = new HashMap<>();
    txs.stream()
        .filter(Transaction::isIncome)
        .forEach(tx -> result.merge(tx.getCategoryName(), tx.getAmount(), Double::sum));
    return result;
  }

  public Map<String, Double> expenseByCategory(Collection<Transaction> txs) {
    Map<String, Double> result = new HashMap<>();
    txs.stream()
        .filter(tx -> !tx.isIncome())
        .forEach(tx -> result.merge(tx.getCategoryName(), tx.getAmount(), Double::sum));
    return result;
  }

  /**
   * Фильтрация по периоду и нескольким категориям. Если from/to == null, соответствующая граница не
   * используется. Если categories пустой или null - берём все категории.
   */
  public List<Transaction> filterByPeriodAndCategories(
      Collection<Transaction> txs, LocalDate from, LocalDate to, Set<String> categories) {
    return txs.stream()
        .filter(tx -> from == null || !tx.getDateTime().toLocalDate().isBefore(from))
        .filter(tx -> to == null || !tx.getDateTime().toLocalDate().isAfter(to))
        .filter(
            tx ->
                categories == null
                    || categories.isEmpty()
                    || categories.contains(tx.getCategoryName()))
        .sorted(new Transaction.DateComparator())
        .collect(Collectors.toList());
  }

  /** Пример использования анонимного класса Comparator: сортировка по сумме по убыванию. */
  public void sortByAmountDesc(List<Transaction> txs) {
    txs.sort((o1, o2) -> Double.compare(o2.getAmount(), o1.getAmount()));
  }

  public enum ReportFormat {
    CSV,
    JSON
  }

  public void exportTransactions(Collection<Transaction> txs, Path path, ReportFormat format) {
    List<Transaction> list = new ArrayList<>(txs);
    switch (format) {
      case CSV -> CsvUtil.writeTransactionsCsv(list, path);
      case JSON -> {
        // Пишем в JSON не напрямую Transaction, а DTO с флагом income
        List<JsonReportTransactionDto> dtoList =
            list.stream().map(JsonReportTransactionDto::new).collect(Collectors.toList());
        JsonFileUtil.writeList(path, dtoList);
      }
      default -> throw new IllegalArgumentException("Неизвестный формат отчёта: " + format);
    }
  }

  public List<Transaction> importTransactions(Path path, ReportFormat format) {
    return switch (format) {
      case CSV -> CsvUtil.readTransactionsCsv(path);
      case JSON -> {
        List<JsonReportTransactionDto> dtoList =
            JsonFileUtil.readList(path, JsonReportTransactionDto.class);
        if (dtoList.isEmpty()) {
          throw new IllegalArgumentException("Файл пуст или не содержит операций: " + path);
        }
        List<Transaction> result = new ArrayList<>();
        int index = 0;
        for (JsonReportTransactionDto dto : dtoList) {
          validateDto(dto, index, path);
          if (dto.isIncome()) {
            result.add(
                new Income(
                    dto.getId(),
                    dto.getAmount(),
                    dto.getDateTime(),
                    dto.getCategoryName(),
                    dto.getDescription()));
          } else {
            result.add(
                new Expense(
                    dto.getId(),
                    dto.getAmount(),
                    dto.getDateTime(),
                    dto.getCategoryName(),
                    dto.getDescription()));
          }
          index++;
        }
        yield result;
      }
    };
  }

  private void validateDto(JsonReportTransactionDto dto, int index, Path path) {
    int recordNumber = getRecordNumber(dto, index, path);
    if (dto.getDateTime() == null) {
      throw new IllegalArgumentException(
          "Ошибка в файле " + path + ", запись " + recordNumber + ": поле 'dateTime' обязательно.");
    }
    if (dto.getAmount() <= 0) {
      throw new IllegalArgumentException(
          "Ошибка в файле "
              + path
              + ", запись "
              + recordNumber
              + ": поле 'amount' должно быть > 0, получено "
              + dto.getAmount());
    }
    // description может быть null/пустым — это допустимо
    // income (boolean) Jackson подставит false по умолчанию, но для отчёта это валидный случай
  }

  private static int getRecordNumber(JsonReportTransactionDto dto, int index, Path path) {
    int recordNumber = index + 1; // для сообщений

    if (dto == null) {
      throw new IllegalArgumentException(
          "Ошибка в файле " + path + ", запись " + recordNumber + ": пустой объект операции.");
    }
    if (dto.getId() == null || dto.getId().trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Ошибка в файле " + path + ", запись " + recordNumber + ": поле 'id' обязательно.");
    }
    if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Ошибка в файле "
              + path
              + ", запись "
              + recordNumber
              + ": поле 'categoryName' обязательно.");
    }
    return recordNumber;
  }

  /** DTO для JSON-отчётов. Используется только для экспорта/импорта */
  public static class JsonReportTransactionDto {
    private String id;
    private double amount;
    private LocalDateTime dateTime;
    private String categoryName;
    private String description;
    private boolean income;

    public JsonReportTransactionDto() {}

    public JsonReportTransactionDto(Transaction tx) {
      this.id = tx.getId();
      this.amount = tx.getAmount();
      this.dateTime = tx.getDateTime();
      this.categoryName = tx.getCategoryName();
      this.description = tx.getDescription();
      this.income = tx.isIncome();
    }

    public String getId() {
      return id;
    }

    public double getAmount() {
      return amount;
    }

    public LocalDateTime getDateTime() {
      return dateTime;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public String getDescription() {
      return description;
    }

    public boolean isIncome() {
      return income;
    }

    public void setId(String id) {
      this.id = id;
    }

    public void setAmount(double amount) {
      this.amount = amount;
    }

    public void setDateTime(LocalDateTime dateTime) {
      this.dateTime = dateTime;
    }

    public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setIncome(boolean income) {
      this.income = income;
    }
  }
}
