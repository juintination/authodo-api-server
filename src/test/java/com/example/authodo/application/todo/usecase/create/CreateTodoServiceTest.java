package com.example.authodo.application.todo.usecase.create;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.authodo.application.todo.dto.command.CreateTodoCommand;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTodoService 단위 테스트")
class CreateTodoServiceTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private CreateTodoService createTodoService;

    @Test
    @DisplayName("create() - 새로운 Todo 생성")
    void create_success() {
        CreateTodoCommand command = new CreateTodoCommand("제목", "내용");

        Todo saved = Todo.create(1L, "제목", "내용").toBuilder().id(1L).build();
        given(todoRepositoryPort.save(any(Todo.class))).willReturn(saved);

        Todo result = createTodoService.create(1L, command);

        verify(todoRepositoryPort).save(any(Todo.class));
    }

}
