package com.example.authodo.application.todo.usecase.delete;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

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
@DisplayName("DeleteTodoService 단위 테스트")
class DeleteTodoServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private DeleteTodoService deleteTodoService;

    @Test
    @DisplayName("delete() - 성공")
    void delete_success() {
        Todo todo = Todo.create(1L, "삭제", "내용").toBuilder().id(1L).build();

        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.of(todo));

        deleteTodoService.delete(1L, 1L);

        verify(todoRepositoryPort).deleteById(1L);
    }

    @Test
    @DisplayName("delete() - 존재하지 않으면 예외")
    void delete_notFound() {
        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteTodoService.delete(1L, 1L))
            .isInstanceOf(BusinessException.class);
    }
}
