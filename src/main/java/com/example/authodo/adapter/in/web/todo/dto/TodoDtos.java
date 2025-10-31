package com.example.authodo.adapter.in.web.todo.dto;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

public class TodoDtos {

    public record CreateRequestDTO(
            @NotBlank(message = "{todo.title.notBlank}")
            @Size(max = 200, message = "{todo.title.size.max}")
            String title,
            String content
    ) {}

    public record UpdateRequestDTO(
            @Size(max = 200, message = "{todo.title.size.max}")
            String title,
            String content,
            TodoStatus status
    ) {}

    public record CreateResponseDTO(Long id) {}
    public record DeleteResponseDTO(Long id) {}

    @Builder
    public record ResponseDTO(
            Long id,
            String title,
            String content,
            TodoStatus status,
            boolean completed,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        public static ResponseDTO from(Todo todo) {
            return ResponseDTO.builder()
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
