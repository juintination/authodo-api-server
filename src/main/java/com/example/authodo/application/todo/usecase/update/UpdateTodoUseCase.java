package com.example.authodo.application.todo.usecase.update;

import com.example.authodo.application.todo.dto.command.UpdateTodoCommand;

public interface UpdateTodoUseCase {

    void update(Long userId, Long todoId, UpdateTodoCommand command);
}
