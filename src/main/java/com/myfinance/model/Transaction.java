package com.myfinance.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.Comparator;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Income.class, name = "INCOME"),
  @JsonSubTypes.Type(value = Expense.class, name = "EXPENSE")
})
public abstract class Transaction {

  private String id;
  private double amount;
  private LocalDateTime dateTime;
  private String categoryName;
  private String description;

  public Transaction() {
    // for JSON
  }

  protected Transaction(
      String id, double amount, LocalDateTime dateTime, String categoryName, String description) {
    this.id = id;
    this.amount = amount;
    this.dateTime = dateTime;
    this.categoryName = categoryName;
    this.description = description;
  }

  public abstract boolean isIncome();

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

  public static class DateComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction a, Transaction b) {
      return a.getDateTime().compareTo(b.getDateTime());
    }
  }

  public static class AmountComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction a, Transaction b) {
      return Double.compare(a.getAmount(), b.getAmount());
    }
  }
}
