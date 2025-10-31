package com.example.authodo.application.todo.usecase;

import com.example.authodo.domain.todo.Todo;

public interface GetTodoUseCase {
    Todo get(Long id);
}
