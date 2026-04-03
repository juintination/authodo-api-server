package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.application.todo.dto.result.GetTodosResult;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTodosService implements GetTodosUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public GetTodosResult getTodos(Long userId, GetTodosQuery query) {
        int page = query.page();
        int size = query.size();
        TodoStatus status = query.status();

        List<Todo> todos;
        long totalCount;

        if (status == null) {
            todos = todoRepositoryPort.findAllByUserIdPaged(userId, page, size);
            totalCount = todoRepositoryPort.countByUserId(userId);
        } else {
            todos = todoRepositoryPort.findAllByUserIdAndStatusPaged(userId, status, page, size);
            totalCount = todoRepositoryPort.countByUserIdAndStatus(userId, status);
        }

        return new GetTodosResult(todos, page, size, totalCount);
    }
}
