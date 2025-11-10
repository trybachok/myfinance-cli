package com.myfinance.model;

public class Budget {

  private String categoryName;
  private double limit;

  public Budget() {}

  public Budget(String categoryName, double limit) {
    this.categoryName = categoryName;
    this.limit = limit;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public double getLimit() {
    return limit;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void setLimit(double limit) {
    this.limit = limit;
  }
}
