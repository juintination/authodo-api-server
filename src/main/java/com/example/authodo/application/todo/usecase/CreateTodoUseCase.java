package com.example.authodo.application.todo.usecase;

import com.example.authodo.domain.todo.Todo;

public interface CreateTodoUseCase {
    Todo create(String title, String content);
}
