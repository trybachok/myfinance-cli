package com.myfinance.model;

import java.util.Objects;

public class User {

  private String login;
  private String passwordHash;

  public User() {
    // for JSON
  }

  public User(String login, String passwordHash) {
    this.login = login;
    this.passwordHash = passwordHash;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    return Objects.equals(login, user.login);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login);
  }
}
