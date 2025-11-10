package com.myfinance.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.User;
import com.myfinance.repository.impl.FileUserRepository;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUserRepositoryTest {

  @TempDir Path tempDir;

  @Test
  void saveAndFindUserByLogin() {
    Path usersFile = tempDir.resolve("users-test.json");
    FileUserRepository repo = new FileUserRepository(usersFile);

    User user = new User("testUser", "hash");
    repo.save(user);

    assertTrue(repo.findByLogin("testUser").isPresent());
    assertEquals("hash", repo.findByLogin("testUser").get().getPasswordHash());
  }
}
