package com.example.authodo.domain.todo.port;

import com.example.authodo.domain.todo.Todo;
import java.util.List;
import java.util.Optional;

public interface TodoUseCasePort {
    Todo create(String title, String content);
    Optional<Todo> get(Long id);
    List<Todo> getAll();
    Todo update(Long id, String title, String content, String status);
    void delete(Long id);
}
