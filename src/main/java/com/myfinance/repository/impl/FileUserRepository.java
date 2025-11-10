package com.myfinance.repository.impl;

import com.myfinance.model.User;
import com.myfinance.repository.UserRepository;
import com.myfinance.util.JsonFileUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUserRepository implements UserRepository {

  private final Path usersFile;
  private final List<User> cache = new ArrayList<>();

  /** Боевой конструктор — использует файл src/db/users.json. */
  public FileUserRepository() {
    this(Paths.get("src", "db", "users.json"));
  }

  /** Конструктор для тестов и кастомных конфигураций: можно передать свой путь к файлу. */
  public FileUserRepository(Path usersFile) {
    this.usersFile = usersFile;
    initStorage();
    load();
  }

  private void initStorage() {
    try {
      Path dir = usersFile.getParent();
      if (dir != null && !Files.exists(dir)) {
        Files.createDirectories(dir);
      }
      if (!Files.exists(usersFile)) {
        // создаём пустой JSON-массив
        Files.writeString(usersFile, "[]");
      }
    } catch (Exception e) {
      throw new IllegalStateException(
          "Не удалось инициализировать хранилище пользователей: " + usersFile, e);
    }
  }

  private void load() {
    List<User> users = JsonFileUtil.readList(usersFile, User.class);
    cache.clear();
    cache.addAll(users);
  }

  private void persist() {
    JsonFileUtil.writeList(usersFile, cache);
  }

  @Override
  public Optional<User> findByLogin(String login) {
    if (login == null) {
      return Optional.empty();
    }
    return cache.stream().filter(u -> u.getLogin().equals(login)).findFirst();
  }

  @Override
  public void save(User user) {
    if (user == null || user.getLogin() == null) {
      throw new IllegalArgumentException("Нельзя сохранить пользователя без логина.");
    }
    if (findByLogin(user.getLogin()).isPresent()) {
      throw new IllegalArgumentException("Пользователь с таким логином уже существует");
    }
    cache.add(user);
    persist();
  }

  @Override
  public void update(User user) {
    if (user == null || user.getLogin() == null) {
      throw new IllegalArgumentException("Нельзя обновить пользователя без логина.");
    }
    // удаляем старую запись, если есть
    cache.removeIf(u -> u.getLogin().equals(user.getLogin()));
    cache.add(user);
    persist();
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(cache);
  }
}
