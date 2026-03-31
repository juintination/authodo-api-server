package com.example.authodo.adapter.in.web.todo.dto;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

public class TodoDtos {

    public record TodoCreateRequest(
        @NotBlank(message = "{todo.title.notBlank}")
        @Size(max = 200, message = "{todo.title.size.max}")
        String title,
        String content
    ) {

    }

    public record TodoUpdateRequest(
        @Size(max = 200, message = "{todo.title.size.max}")
        String title,
        String content,
        TodoStatus status
    ) {

    }

    @Builder
    public record TodoResponse(
        Long id,
        String title,
        String content,
        TodoStatus status,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
    ) {

        public static TodoResponse from(Todo todo) {
            return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .content(todo.getContent())
                .status(todo.getStatus())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .modifiedAt(todo.getModifiedAt())
                .build();
        }
    }
}
