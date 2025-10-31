package com.example.authodo.application.todo;

import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.TodoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoService 단위 테스트")
class TodoServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private TodoService todoService;

    private Todo sampleTodo(Long id, String title, String content, TodoStatus status) {
        return Todo.create(title, content).toBuilder()
                .id(id)
                .status(status)
                .completed(status == TodoStatus.COMPLETED)
                .build();
    }

    @Test
    @DisplayName("create() - 새로운 Todo 생성")
    void create_success() {
        // given
        Todo todo = sampleTodo(null, "새로운 제목", "내용", TodoStatus.PENDING);
        Todo saved = todo.toBuilder().id(1L).build();
        given(todoRepositoryPort.save(any(Todo.class))).willReturn(saved);

        // when
        Todo result = todoService.create("새로운 제목", "내용");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(TodoStatus.PENDING);
        verify(todoRepositoryPort).save(any(Todo.class));
    }

    @Test
    @DisplayName("get() - 존재하는 Todo 반환")
    void get_success() {
        // given
        Todo todo = sampleTodo(1L, "제목", "내용", TodoStatus.PENDING);
        given(todoRepositoryPort.findById(1L)).willReturn(Optional.of(todo));

        // when
        Todo result = todoService.get(1L);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.isCompleted()).isFalse();
        verify(todoRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("get() - 존재하지 않으면 예외 발생")
    void get_notFound() {
        // given
        given(todoRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> todoService.get(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TODO_NOT_FOUND.name());
        verify(todoRepositoryPort).findById(999L);
    }

    @Test
    @DisplayName("getAll() - 전체 Todo 목록 반환")
    void getAll_success() {
        // given
        List<Todo> todos = List.of(
                sampleTodo(1L, "첫번째", "내용1", TodoStatus.PENDING),
                sampleTodo(2L, "두번째", "내용2", TodoStatus.COMPLETED)
        );
        given(todoRepositoryPort.findAll()).willReturn(todos);

        // when
        List<Todo> result = todoService.getAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1).isCompleted()).isTrue();
        verify(todoRepositoryPort).findAll();
    }

    @Test
    @DisplayName("update() - 제목, 내용, 상태 수정")
    void update_success() {
        // given
        Todo existing = sampleTodo(1L, "기존 제목", "기존 내용", TodoStatus.PENDING);
        Todo updated = existing.change("새 제목", "새 내용", TodoStatus.COMPLETED);
        given(todoRepositoryPort.findById(1L)).willReturn(Optional.of(existing));
        given(todoRepositoryPort.save(any(Todo.class))).willReturn(updated);

        // when
        todoService.update(1L, "새 제목", "새 내용", TodoStatus.COMPLETED);

        // then
        verify(todoRepositoryPort).findById(1L);
        verify(todoRepositoryPort).save(any(Todo.class));
    }

    @Test
    @DisplayName("update() - 존재하지 않으면 예외 발생")
    void update_notFound() {
        // given
        given(todoRepositoryPort.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                todoService.update(1L, "수정", "내용", TodoStatus.PENDING))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TODO_NOT_FOUND.name());
    }

    @Test
    @DisplayName("delete() - 존재하는 Todo 삭제")
    void delete_success() {
        // given
        Todo todo = sampleTodo(1L, "삭제할 Todo", "내용", TodoStatus.PENDING);
        given(todoRepositoryPort.findById(1L)).willReturn(Optional.of(todo));
        willDoNothing().given(todoRepositoryPort).deleteById(1L);

        // when
        todoService.delete(1L);

        // then
        verify(todoRepositoryPort).deleteById(1L);
    }

    @Test
    @DisplayName("delete() - 존재하지 않으면 예외 발생")
    void delete_notFound() {
        // given
        given(todoRepositoryPort.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> todoService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.TODO_NOT_FOUND.name());
    }

}
