package com.example.authodo.application.todo.usecase.get;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.application.todo.dto.query.GetTodosQuery;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTodosService 단위 테스트")
class GetTodosServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private GetTodosService getTodosService;

    @Test
    @DisplayName("getTodos() - 페이지네이션")
    void getTodos_all() {
        List<Todo> todos = List.of(
            Todo.create(1L, "a", "a"),
            Todo.create(1L, "b", "b")
        );

        given(todoRepositoryPort.findAllByUserIdPaged(1L, 1, 20)).willReturn(todos);
        given(todoRepositoryPort.countByUserId(1L)).willReturn(2L);

        GetTodosQuery query = new GetTodosQuery(1, 20, null);

        getTodosService.getTodos(1L, query);

        verify(todoRepositoryPort).findAllByUserIdPaged(1L, 1, 20);
        verify(todoRepositoryPort).countByUserId(1L);
    }

    @Test
    @DisplayName("getTodos() - status 필터 조회")
    void getTodos_withStatus() {
        List<Todo> todos = List.of(
            Todo.create(1L, "a", "a").toBuilder().status(TodoStatus.COMPLETED).build()
        );

        given(todoRepositoryPort.findAllByUserIdAndStatusPaged(1L, TodoStatus.COMPLETED, 1, 20))
            .willReturn(todos);
        given(todoRepositoryPort.countByUserIdAndStatus(1L, TodoStatus.COMPLETED))
            .willReturn(1L);

        GetTodosQuery query = new GetTodosQuery(1, 20, TodoStatus.COMPLETED);

        getTodosService.getTodos(1L, query);

        verify(todoRepositoryPort).findAllByUserIdAndStatusPaged(1L, TodoStatus.COMPLETED, 1, 20);
        verify(todoRepositoryPort).countByUserIdAndStatus(1L, TodoStatus.COMPLETED);
    }
}
