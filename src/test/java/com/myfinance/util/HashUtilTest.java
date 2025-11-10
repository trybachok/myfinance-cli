package com.myfinance.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HashUtilTest {

  @Test
  void hashShouldBeDeterministic() {
    String h1 = HashUtil.hashPassword("secret");
    String h2 = HashUtil.hashPassword("secret");
    assertEquals(h1, h2);
  }

  @Test
  void verifyPasswordShouldReturnTrueForCorrectPassword() {
    String hash = HashUtil.hashPassword("secret");
    assertTrue(HashUtil.verifyPassword("secret", hash));
  }

  @Test
  void verifyPasswordShouldReturnFalseForWrongPassword() {
    String hash = HashUtil.hashPassword("secret");
    assertFalse(HashUtil.verifyPassword("other", hash));
  }
}
