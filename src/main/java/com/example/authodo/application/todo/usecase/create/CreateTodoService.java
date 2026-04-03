package com.example.authodo.application.todo.usecase.create;

import com.example.authodo.application.todo.dto.command.CreateTodoCommand;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTodoService implements CreateTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    @Override
    @Transactional
    public Todo create(Long userId, CreateTodoCommand command) {
        Todo todo = Todo.create(
            userId,
            command.title(),
            command.content()
        );
        return todoRepositoryPort.save(todo);
    }
}
