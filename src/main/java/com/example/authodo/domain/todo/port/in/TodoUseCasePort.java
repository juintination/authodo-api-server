package com.example.authodo.domain.todo.port.in;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import java.util.List;

public interface TodoUseCasePort {

    Todo create(Long userId, String title, String content);

    Todo get(Long userId, Long id);

    List<Todo> getAll(Long userId);

    void update(Long userId, Long id, String title, String content, TodoStatus status);

    void delete(Long userId, Long id);
}
