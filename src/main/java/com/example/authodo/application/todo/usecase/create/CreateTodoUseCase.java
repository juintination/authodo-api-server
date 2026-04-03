package com.example.authodo.application.todo.usecase.create;

import com.example.authodo.application.todo.dto.command.CreateTodoCommand;
import com.example.authodo.domain.todo.Todo;

public interface CreateTodoUseCase {

    Todo create(Long userId, CreateTodoCommand command);
}
