package com.example.authodo.adapter.in.web.todo;

import com.example.authodo.adapter.in.web.todo.dto.TodoDtos;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.CreateResponseDTO;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.DeleteResponseDTO;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.ResponseDTO;
import com.example.authodo.common.response.ApiResponse;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.TodoUseCasePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoUseCasePort todoUseCasePort;

    @PostMapping
    public ApiResponse<CreateResponseDTO> create(@Valid @RequestBody TodoDtos.CreateRequestDTO req) {
        Todo saved = todoUseCasePort.create(req.title(), req.content());
        return ApiResponse.of(new CreateResponseDTO(saved.getId()), "registerSuccess");
    }

    @GetMapping("/{id}")
    public ApiResponse<ResponseDTO> get(@PathVariable Long id) {
        Todo todo = todoUseCasePort.get(id);
        return ApiResponse.of(ResponseDTO.from(todo), "getSuccess");
    }

    @GetMapping
    public ApiResponse<List<ResponseDTO>> getAll() {
        List<ResponseDTO> list = todoUseCasePort.getAll().stream()
                .map(ResponseDTO::from)
                .toList();
        return ApiResponse.of(list, "getAllSuccess");
    }

    @PutMapping("/{id}")
    public ApiResponse<ResponseDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody TodoDtos.UpdateRequestDTO req) {
        todoUseCasePort.update(id, req.title(), req.content(), req.status());
        Todo updated = todoUseCasePort.get(id);
        return ApiResponse.of(ResponseDTO.from(updated), "updateSuccess");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<DeleteResponseDTO> delete(@PathVariable Long id) {
        todoUseCasePort.delete(id);
        return ApiResponse.of(new DeleteResponseDTO(id), "deleteSuccess");
    }

}
