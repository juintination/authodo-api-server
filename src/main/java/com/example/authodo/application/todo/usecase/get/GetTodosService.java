package com.example.authodo.application.todo.usecase.get;

import com.example.authodo.application.common.pagination.PageResult;
import com.example.authodo.application.todo.dto.filter.TodoFilter;
import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.domain.todo.Todo;
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
    public PageResult<Todo> getTodos(Long userId, GetTodosQuery query) {
        int page = query.pageQuery().page();
        int size = query.pageQuery().size();
        TodoFilter filter = query.pageQuery().filter();

        List<Todo> todos;
        long totalCount;

        if (filter.status() == null) {
            todos = todoRepositoryPort.findAllByUserIdPaged(
                userId,
                page,
                size
            );
            totalCount = todoRepositoryPort.countByUserId(userId);
        } else {
            todos = todoRepositoryPort.findAllByUserIdAndStatusPaged(
                userId,
                filter.status(),
                page,
                size
            );
            totalCount = todoRepositoryPort.countByUserIdAndStatus(userId, filter.status());
        }

        return new PageResult<>(
            todos,
            totalCount
        );
    }
}
