package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.application.todo.dto.result.GetTodosResult;

public interface GetTodosUseCase {

    GetTodosResult getTodos(Long userId, GetTodosQuery query);
}
