package com.example.authodo.application.todo.usecase.delete;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteTodoService implements DeleteTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional
    public void delete(Long userId, Long todoId) {
        if (todoRepositoryPort.findByUserIdAndId(userId, todoId).isEmpty()) {
            throw new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId);
        }
        todoRepositoryPort.deleteById(todoId);
    }
}
