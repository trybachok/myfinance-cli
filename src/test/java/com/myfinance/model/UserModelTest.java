package com.myfinance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserModelTest {

  @Test
  void constructorAndGettersShouldWork() {
    User user = new User("someLogin", "someHash");

    assertEquals("someLogin", user.getLogin());
    assertEquals("someHash", user.getPasswordHash());
  }
}
