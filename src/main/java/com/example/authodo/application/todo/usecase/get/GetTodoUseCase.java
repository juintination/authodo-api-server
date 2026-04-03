package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.domain.todo.Todo;

public interface GetTodoUseCase {

    Todo get(Long userId, Long todoId);
}
