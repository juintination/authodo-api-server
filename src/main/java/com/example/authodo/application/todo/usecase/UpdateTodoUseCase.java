package com.example.authodo.application.todo.usecase;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;

public interface UpdateTodoUseCase {
    Todo update(Long id, String title, String content, TodoStatus status);
}
