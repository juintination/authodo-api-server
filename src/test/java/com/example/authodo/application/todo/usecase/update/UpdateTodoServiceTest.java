package com.example.authodo.application.todo.usecase.update;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.application.todo.dto.command.UpdateTodoCommand;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTodoService 단위 테스트")
class UpdateTodoServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private UpdateTodoService updateTodoService;

    @Test
    @DisplayName("update() - 성공")
    void update_success() {
        Todo existing = Todo.create(1L, "기존", "내용").toBuilder().id(1L).build();

        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.of(existing));
        given(todoRepositoryPort.save(any(Todo.class))).willReturn(existing);

        UpdateTodoCommand command = new UpdateTodoCommand("새 제목", "새 내용", TodoStatus.COMPLETED);

        updateTodoService.update(1L, 1L, command);

        verify(todoRepositoryPort).save(any(Todo.class));
    }

    @Test
    @DisplayName("update() - 존재하지 않으면 예외")
    void update_notFound() {
        given(todoRepositoryPort.findByUserIdAndId(1L, 1L)).willReturn(Optional.empty());

        UpdateTodoCommand command = new UpdateTodoCommand("제목", "내용", TodoStatus.PENDING);

        assertThatThrownBy(() -> updateTodoService.update(1L, 1L, command))
            .isInstanceOf(BusinessException.class);
    }
}
