package com.myfinance.repository;

import com.myfinance.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findByLogin(String login);

  void save(User user);

  void update(User user);

  List<User> findAll();
}
