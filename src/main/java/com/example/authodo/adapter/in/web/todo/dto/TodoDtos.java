package com.example.authodo.adapter.in.web.todo.dto;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public record ResponseDTO(
            Long id,
            String title,
            String content,
            TodoStatus status,
            boolean completed,
            String createdAt,
            String modifiedAt
    ) {
        public static ResponseDTO from(TodoJpaEntity e) {
            return new ResponseDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getContent(),
                    e.getStatus(),
                    e.isCompleted(),
                    e.getCreatedAt() == null ? null : e.getCreatedAt().toString(),
                    e.getModifiedAt() == null ? null : e.getModifiedAt().toString()
            );
        }
    }

}
