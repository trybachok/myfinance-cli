package com.myfinance.util;

import static org.junit.jupiter.api.Assertions.*;

import com.myfinance.model.User;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonFileUtilTest {

  @TempDir Path tempDir;

  @Test
  void readListShouldReturnEmptyForMissingFile() {
    Path file = tempDir.resolve("missing.json");
    List<User> users = JsonFileUtil.readList(file, User.class);
    assertNotNull(users);
    assertTrue(users.isEmpty());
  }

  @Test
  void writeAndReadListShouldWork() {
    Path file = tempDir.resolve("users.json");
    List<User> original = List.of(new User("u1", "h1"), new User("u2", "h2"));
    JsonFileUtil.writeList(file, original);

    List<User> read = JsonFileUtil.readList(file, User.class);
    assertEquals(2, read.size());
    assertEquals("u1", read.get(0).getLogin());
    assertEquals("h2", read.get(1).getPasswordHash());
  }

  @Test
  void writeAndReadObjectShouldWork() {
    Path file = tempDir.resolve("user.json");
    User user = new User("single", "hash");
    JsonFileUtil.writeObject(file, user);

    User read = JsonFileUtil.readObject(file, User.class, null);
    assertNotNull(read);
    assertEquals("single", read.getLogin());
  }
}
