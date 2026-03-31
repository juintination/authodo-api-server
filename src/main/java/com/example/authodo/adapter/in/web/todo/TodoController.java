package com.example.authodo.adapter.in.web.todo;

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

    @PostMapping
    public ApiResponse<Long> create(
        @Valid @RequestBody TodoCreateRequest request
    ) {
        Todo result = todoUseCasePort.create(request.title(), request.content());
        return ApiResponse.success(result.getId());
    }

    @GetMapping("/{id}")
    public ApiResponse<TodoResponse> get(
        @PathVariable Long id
    ) {
        Todo result = todoUseCasePort.get(id);
        return ApiResponse.success(TodoResponse.from(result));
    }

    @GetMapping
    public ApiResponse<List<TodoResponse>> getAll() {
        List<TodoResponse> result = todoUseCasePort.getAll().stream()
            .map(TodoResponse::from)
            .toList();
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse update(
        @PathVariable Long id,
        @Valid @RequestBody TodoUpdateRequest request
    ) {
        todoUseCasePort.update(id, request.title(), request.content(), request.status());
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(
        @PathVariable Long id
    ) {
        todoUseCasePort.delete(id);
        return ApiResponse.success(null);
    }
}
