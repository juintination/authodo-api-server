package com.example.authodo.adapter.in.web.todo;

import com.example.authodo.adapter.in.web.common.response.ApiResponse;
import com.example.authodo.adapter.in.web.common.response.PageResponse;
import com.example.authodo.adapter.in.web.security.annotation.AuthenticatedUserId;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.GetTodosRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoCreateRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoResponse;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoUpdateRequest;
import com.example.authodo.application.common.pagination.PageResult;
import com.example.authodo.application.todo.usecase.create.CreateTodoUseCase;
import com.example.authodo.application.todo.usecase.delete.DeleteTodoUseCase;
import com.example.authodo.application.todo.usecase.get.GetTodoUseCase;
import com.example.authodo.application.todo.usecase.get.GetTodosUseCase;
import com.example.authodo.application.todo.usecase.update.UpdateTodoUseCase;
import com.example.authodo.domain.todo.Todo;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final CreateTodoUseCase createTodoUseCase;
    private final GetTodoUseCase getTodoUseCase;
    private final GetTodosUseCase getTodosUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;

    @PostMapping
    public ApiResponse<Long> create(
        @AuthenticatedUserId Long userId,
        @Valid @RequestBody TodoCreateRequest request
    ) {
        Long result = createTodoUseCase.create(userId, request.toCommand()).getId();

        return ApiResponse.success(result);
    }

    @GetMapping("/{todoId}")
    public ApiResponse<TodoResponse> get(
        @AuthenticatedUserId Long userId,
        @PathVariable Long todoId
    ) {
        Todo result = getTodoUseCase.get(userId, todoId);

        return ApiResponse.success(TodoResponse.from(result));
    }

    @GetMapping
    public ApiResponse<PageResponse<TodoResponse>> getTodos(
        @AuthenticatedUserId Long userId,
        @Valid GetTodosRequest request
    ) {
        PageResult<Todo> result = getTodosUseCase.getTodos(userId, request.toQuery());

        List<TodoResponse> items = result.items().stream()
            .map(TodoResponse::from)
            .toList();

        return ApiResponse.success(
            PageResponse.of(
                items,
                request.page(),
                request.size(),
                result.totalCount()
            )
        );
    }

    @PutMapping("/{todoId}")
    public ApiResponse<Void> update(
        @AuthenticatedUserId Long userId,
        @PathVariable Long todoId,
        @Valid @RequestBody TodoUpdateRequest request
    ) {
        updateTodoUseCase.update(userId, todoId, request.toCommand());

        return ApiResponse.success(null);
    }

    @DeleteMapping("/{todoId}")
    public ApiResponse<Void> delete(
        @AuthenticatedUserId Long userId,
        @PathVariable Long todoId
    ) {
        deleteTodoUseCase.delete(userId, todoId);

        return ApiResponse.success(null);
    }
}
