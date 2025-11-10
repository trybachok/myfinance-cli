package com.myfinance.repository.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class DbPaths {

  static final Path DB_DIR = Paths.get("src", "db");
  static final Path USERS_FILE = DB_DIR.resolve("users.json");
  static final Path WALLETS_DIR = DB_DIR.resolve("wallets");

  static {
    init();
  }

  private DbPaths() {}

  private static void init() {
    try {
      if (!Files.exists(DB_DIR)) {
        Files.createDirectories(DB_DIR);
      }
      if (!Files.exists(WALLETS_DIR)) {
        Files.createDirectories(WALLETS_DIR);
      }
      if (!Files.exists(USERS_FILE)) {
        Files.writeString(USERS_FILE, "[]");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось инициализировать директорию db", e);
    }
  }
}
