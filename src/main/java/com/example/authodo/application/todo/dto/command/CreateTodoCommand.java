package com.example.authodo.application.todo.dto.command;

public record CreateTodoCommand(
    String title,
    String content
) {

}
