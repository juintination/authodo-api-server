package com.example.authodo.domain.todo.port.in;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;

import java.util.List;

public interface TodoUseCasePort {
    Todo create(String title, String content);
    Todo get(Long id);
    List<Todo> getAll();
    void update(Long id, String title, String content, TodoStatus status);
    void delete(Long id);
}
