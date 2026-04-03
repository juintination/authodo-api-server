package com.example.authodo.application.todo.usecase.get;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTodoService 단위 테스트")
class GetTodoServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private GetTodoService getTodoService;

    @Test
    @DisplayName("get() - 존재하는 Todo 반환")
    void get_success() {
        Todo todo = Todo.create(1L, "제목", "내용").toBuilder().id(1L).build();
        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.of(todo));

        getTodoService.get(1L, 1L);

        verify(todoRepositoryPort).findByUserIdAndId(1L, 1L);
    }

    @Test
    @DisplayName("get() - 존재하지 않으면 예외 발생")
    void get_notFound() {
        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getTodoService.get(1L, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.TODO_NOT_FOUND.name());
    }
}
