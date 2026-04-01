package com.example.authodo.application.todo;

import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.in.TodoUseCasePort;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService implements TodoUseCasePort {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional
    public Todo create(Long userId, String title, String content) {
        Todo todo = Todo.create(userId, title, content);
        return todoRepositoryPort.save(todo);
    }

    @Override
    public Todo get(Long userId, Long todoId) {
        return todoRepositoryPort.findByUserIdAndId(userId, todoId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId));
    }

    @Override
    public List<Todo> getAll(Long userId) {
        return todoRepositoryPort.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void update(Long userId, Long todoId, String title, String content, TodoStatus status) {
        Todo existing = todoRepositoryPort.findByUserIdAndId(userId, todoId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId));
        Todo updated = existing.change(title, content, status);
        todoRepositoryPort.save(updated);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long todoId) {
        if (todoRepositoryPort.findByUserIdAndId(userId, todoId).isEmpty()) {
            throw new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId);
        }
        todoRepositoryPort.deleteById(todoId);
    }
}
