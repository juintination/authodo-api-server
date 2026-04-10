package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.application.common.pagination.PageResult;
import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.domain.todo.Todo;

public interface GetTodosUseCase {

    PageResult<Todo> getTodos(Long userId, GetTodosQuery query);
}
