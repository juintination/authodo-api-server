package com.example.authodo.adapter.out.persistence.jpa.converter;

import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoStatusConverter extends BaseEnumConverter<TodoStatus> {
    public TodoStatusConverter() {
        super(TodoStatus.class);
    }
}
