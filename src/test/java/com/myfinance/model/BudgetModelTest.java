package com.myfinance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BudgetModelTest {

  @Test
  void constructorGettersAndSettersShouldWork() {
    Budget budget = new Budget("Еда", 4000.0);

    assertEquals("Еда", budget.getCategoryName());
    assertEquals(4000.0, budget.getLimit(), 0.0001);

    budget.setCategoryName("Продукты");
    budget.setLimit(5000.0);

    assertEquals("Продукты", budget.getCategoryName());
    assertEquals(5000.0, budget.getLimit(), 0.0001);
  }
}
