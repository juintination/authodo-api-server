package com.example.authodo.adapter.in.web.todo;

import com.example.authodo.adapter.in.web.todo.dto.TodoDtos;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.ResponseDTO;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.CreateResponseDTO;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.DeleteResponseDTO;
import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.application.todo.TodoService;
import com.example.authodo.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ApiResponse<CreateResponseDTO> create(@Valid @RequestBody TodoDtos.CreateRequestDTO req) {
        Long id = todoService.create(req.title(), req.content());
        return ApiResponse.of(new CreateResponseDTO(id), "registerSuccess");
    }

    @GetMapping("/{id}")
    public ApiResponse<ResponseDTO> get(@PathVariable Long id) {
        TodoJpaEntity todoJpaEntity = todoService.get(id);
        return ApiResponse.of(ResponseDTO.from(todoJpaEntity), "getSuccess");
    }

    @GetMapping
    public ApiResponse<List<ResponseDTO>> getAll() {
        List<ResponseDTO> responseDtoList = todoService.getAll().stream().map(ResponseDTO::from).toList();
        return ApiResponse.of(responseDtoList, "getAllSuccess");
    }

    @PutMapping("/{id}")
    public ApiResponse<ResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TodoDtos.UpdateRequestDTO req) {
        todoService.update(id, req.title(), req.content(), req.status());
        TodoJpaEntity updated = todoService.get(id);
        return ApiResponse.of(ResponseDTO.from(updated), "updateSuccess");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<DeleteResponseDTO> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ApiResponse.of(new DeleteResponseDTO(id), "deleteSuccess");
    }

}
