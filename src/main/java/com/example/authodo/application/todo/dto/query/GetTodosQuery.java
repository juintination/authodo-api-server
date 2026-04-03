package com.example.authodo.application.todo.dto.query;

import com.example.authodo.domain.todo.enums.TodoStatus;

public record GetTodosQuery(
    int page,
    int size,
    TodoStatus status
) {

}
