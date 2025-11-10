package com.myfinance.service;

import com.myfinance.model.User;
import com.myfinance.repository.UserRepository;
import com.myfinance.util.HashUtil;
import java.util.regex.Pattern;

public class AuthService {

  private final UserRepository userRepository;

  // простой паттерн для логина: буквы, цифры, _ , длина 3-32
  private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,32}$");

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User register(String login, String rawPassword) {
    validateLogin(login);
    validatePassword(rawPassword);

    userRepository
        .findByLogin(login)
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Логин уже занят.");
            });

    String hash = HashUtil.hashPassword(rawPassword);
    User user = new User(login, hash);
    userRepository.save(user);
    return user;
  }

  public User login(String login, String rawPassword) {
    validateLoginNotEmpty(login);
    validatePasswordNotEmpty(rawPassword);

    User user =
        userRepository
            .findByLogin(login)
            .orElseThrow(
                () -> new IllegalArgumentException("Пользователь с таким логином не найден."));

    if (!HashUtil.verifyPassword(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("Неверный пароль.");
    }
    return user;
  }

  public void resetPassword(String login, String newRawPassword) {
    validateLoginNotEmpty(login);
    validatePassword(newRawPassword);

    User user =
        userRepository
            .findByLogin(login)
            .orElseThrow(
                () -> new IllegalArgumentException("Пользователь с таким логином не найден."));

    String newHash = HashUtil.hashPassword(newRawPassword);
    User updated = new User(user.getLogin(), newHash);
    userRepository.update(updated);
  }

  private void validateLoginNotEmpty(String login) {
    if (login == null || login.isBlank()) {
      throw new IllegalArgumentException("Логин не может быть пустым.");
    }
  }

  private void validatePasswordNotEmpty(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Пароль не может быть пустым.");
    }
  }

  private void validateLogin(String login) {
    validateLoginNotEmpty(login);
    if (!LOGIN_PATTERN.matcher(login).matches()) {
      throw new IllegalArgumentException(
          "Логин должен содержать 3-32 символа: латинские буквы, цифры или _.");
    }
  }

  private void validatePassword(String password) {
    validatePasswordNotEmpty(password);
    if (password.length() < 6) {
      throw new IllegalArgumentException("Пароль должен быть не короче 6 символов.");
    }
  }
}
