package com.example.authodo.application.todo.dto.filter;

import com.example.authodo.application.common.pagination.PageFilter;
import com.example.authodo.domain.todo.enums.TodoStatus;

public record TodoFilter(
    TodoStatus status
) implements PageFilter {

}
