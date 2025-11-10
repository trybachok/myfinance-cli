package com.myfinance.util;

import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public final class JsonFileUtil {

  private JsonFileUtil() {}

  public static <T> List<T> readList(Path path, Class<T> clazz) {
    if (!Files.exists(path)) {
      return Collections.emptyList();
    }
    try {
      String json = Files.readString(path);
      if (json.isBlank()) {
        return Collections.emptyList();
      }
      JavaType type = JsonUtil.mapper().getTypeFactory().constructCollectionType(List.class, clazz);
      return JsonUtil.mapper().readValue(json, type);
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка чтения файла: " + path, e);
    }
  }

  public static <T> void writeList(Path path, List<T> data) {
    try {
      String json = JsonUtil.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(data);
      Files.writeString(path, json);
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка записи файла: " + path, e);
    }
  }

  public static <T> T readObject(Path path, Class<T> clazz, T defaultValue) {
    if (!Files.exists(path)) {
      return defaultValue;
    }
    try {
      String json = Files.readString(path);
      if (json.isBlank()) {
        return defaultValue;
      }
      return JsonUtil.mapper().readValue(json, clazz);
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка чтения файла: " + path, e);
    }
  }

  public static <T> void writeObject(Path path, T value) {
    try {
      String json = JsonUtil.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(value);
      Files.writeString(path, json);
    } catch (IOException e) {
      throw new IllegalStateException("Ошибка записи файла: " + path, e);
    }
  }
}
