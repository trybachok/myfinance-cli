package com.myfinance.service;

import com.myfinance.model.User;
import com.myfinance.repository.UserRepository;
import java.util.*;

public class InMemoryUserRepository implements UserRepository {

  private final Map<String, User> users = new HashMap<>();

  @Override
  public Optional<User> findByLogin(String login) {
    return Optional.ofNullable(users.get(login));
  }

  @Override
  public void save(User user) {
    if (users.containsKey(user.getLogin())) {
      throw new IllegalArgumentException("Пользователь уже существует");
    }
    users.put(user.getLogin(), user);
  }

  @Override
  public void update(User user) {
    users.put(user.getLogin(), user);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(users.values());
  }
}
