package com.example.authodo.application.todo.usecase;

import com.example.authodo.domain.todo.Todo;
import java.util.List;

public interface GetAllTodosUseCase {
    List<Todo> getAll();
}
