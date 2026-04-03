package com.example.authodo.adapter.in.web.todo.dto;

import com.example.authodo.application.todo.dto.command.CreateTodoCommand;
import com.example.authodo.application.todo.dto.command.UpdateTodoCommand;
import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.application.todo.dto.result.GetTodosResult;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class TodoDtos {

    public record TodoCreateRequest(
        @NotBlank(message = "{todo.title.notBlank}")
        @Size(max = 200, message = "{todo.title.size.max}")
        String title,
        String content
    ) {

        public CreateTodoCommand toCommand() {
            return new CreateTodoCommand(this.title, this.content);
        }
    }

    public record GetTodosRequest(
        @Min(value = 1, message = "{pagination.page.min}")
        int page,
        @Min(value = 1, message = "{pagination.size.min}")
        int size,
        TodoStatus status
    ) {

        public GetTodosQuery toQuery() {
            return new GetTodosQuery(this.page, this.size, this.status);
        }
    }

    public record TodoUpdateRequest(
        @Size(max = 200, message = "{todo.title.size.max}")
        String title,
        String content,
        TodoStatus status
    ) {

        public UpdateTodoCommand toCommand() {
            return new UpdateTodoCommand(this.title, this.content, this.status);
        }
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

    public record TodoPageResponse(
        List<TodoResponse> items,
        int page,
        int size,
        long totalCount
    ) {

        public static TodoPageResponse from(GetTodosResult result) {
            List<TodoResponse> items = result.items().stream()
                .map(TodoResponse::from)
                .toList();

            return new TodoPageResponse(
                items,
                result.page(),
                result.size(),
                result.totalCount()
            );
        }
    }
}
