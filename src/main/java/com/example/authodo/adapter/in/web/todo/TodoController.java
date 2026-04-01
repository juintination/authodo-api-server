package com.example.authodo.adapter.in.web.todo;

import com.example.authodo.adapter.in.web.security.util.SecurityUtil;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoCreateRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoResponse;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoUpdateRequest;
import com.example.authodo.common.response.ApiResponse;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.in.TodoUseCasePort;
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

    private final TodoUseCasePort todoUseCasePort;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ApiResponse<Long> create(
        @Valid @RequestBody TodoCreateRequest request
    ) {
        Long userId = securityUtil.getCurrentUserId();

        Todo result = todoUseCasePort.create(
            userId,
            request.title(),
            request.content()
        );

        return ApiResponse.success(result.getId());
    }

    @GetMapping("/{todoId}")
    public ApiResponse<TodoResponse> get(
        @PathVariable Long todoId
    ) {
        Long userId = securityUtil.getCurrentUserId();

        Todo result = todoUseCasePort.get(userId, todoId);

        return ApiResponse.success(TodoResponse.from(result));
    }

    @GetMapping
    public ApiResponse<List<TodoResponse>> getAll() {
        Long userId = securityUtil.getCurrentUserId();

        List<TodoResponse> result = todoUseCasePort.getAll(userId).stream()
            .map(TodoResponse::from)
            .toList();

        return ApiResponse.success(result);
    }

    @PutMapping("/{todoId}")
    public ApiResponse update(
        @PathVariable Long todoId,
        @Valid @RequestBody TodoUpdateRequest request
    ) {
        Long userId = securityUtil.getCurrentUserId();

        todoUseCasePort.update(userId, todoId, request.title(), request.content(), request.status());

        return ApiResponse.success(null);
    }

    @DeleteMapping("/{todoId}")
    public ApiResponse delete(
        @PathVariable Long todoId
    ) {
        Long userId = securityUtil.getCurrentUserId();

        todoUseCasePort.delete(userId, todoId);

        return ApiResponse.success(null);
    }
}
