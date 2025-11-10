package com.myfinance.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

  private HashUtil() {}

  public static String hashPassword(String rawPassword) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Алгоритм SHA-256 недоступен", e);
    }
  }

  public static boolean verifyPassword(String rawPassword, String hash) {
    return hashPassword(rawPassword).equals(hash);
  }
}
