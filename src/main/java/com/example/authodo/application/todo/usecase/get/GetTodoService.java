package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTodoService implements GetTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Todo get(Long userId, Long todoId) {
        return todoRepositoryPort.findByUserIdAndId(userId, todoId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, todoId));
    }
}
