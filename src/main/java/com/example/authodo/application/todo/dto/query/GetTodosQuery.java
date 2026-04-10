package com.example.authodo.application.todo.dto.query;

import com.example.authodo.application.common.pagination.PageQuery;
import com.example.authodo.application.todo.dto.filter.TodoFilter;

public record GetTodosQuery(
    PageQuery<TodoFilter> pageQuery
) {

}
