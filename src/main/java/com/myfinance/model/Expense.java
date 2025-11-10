package com.myfinance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Expense extends Transaction {

  public Expense() {
    // for JSON
  }

  public Expense(
      String id, double amount, LocalDateTime dateTime, String categoryName, String description) {
    super(id, amount, dateTime, categoryName, description);
  }

  @Override
  public boolean isIncome() {
    return false;
  }
}
