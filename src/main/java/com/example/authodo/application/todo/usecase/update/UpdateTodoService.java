package com.example.authodo.application.todo.usecase.update;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.application.todo.dto.command.UpdateTodoCommand;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTodoService implements UpdateTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional
    public void update(Long userId, Long todoId, UpdateTodoCommand command) {
        Todo todo = todoRepositoryPort.findByUserIdAndId(userId, todoId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId));

        Todo updated = todo.change(command.title(), command.content(), command.status());
        todoRepositoryPort.save(updated);
    }
}
