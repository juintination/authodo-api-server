package com.example.authodo.application.todo.dto.result;

import com.example.authodo.domain.todo.Todo;
import java.util.List;

public record GetTodosResult(
    List<Todo> items,
    int page,
    int size,
    long totalCount
) {

}
