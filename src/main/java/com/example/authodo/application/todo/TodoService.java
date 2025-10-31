package com.example.authodo.application.todo;

import com.example.authodo.application.todo.usecase.*;
import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService implements
        CreateTodoUseCase,
        GetTodoUseCase,
        GetAllTodosUseCase,
        UpdateTodoUseCase,
        DeleteTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional
    public Todo create(String title, String content) {
        Todo todo = Todo.create(title, content);
        return todoRepositoryPort.save(todo);
    }

    @Override
    public Todo get(Long id) {
        return todoRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, id));
    }

    @Override
    public List<Todo> getAll() {
        return todoRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public Todo update(Long id, String title, String content, TodoStatus status) {
        Todo existing = get(id);
        Todo updated = existing.change(title, content, status);
        return todoRepositoryPort.save(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (todoRepositoryPort.findById(id).isEmpty()) {
            throw new BusinessException(ErrorCode.TODO_NOT_FOUND, id);
        }
        todoRepositoryPort.deleteById(id);
    }

}
