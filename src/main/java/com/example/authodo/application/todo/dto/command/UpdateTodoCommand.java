package com.example.authodo.application.todo.dto.command;

import com.example.authodo.domain.todo.enums.TodoStatus;

public record UpdateTodoCommand(
    String title,
    String content,
    TodoStatus status
) {

}
