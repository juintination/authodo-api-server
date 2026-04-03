package com.example.authodo.domain.todo.port.out;

import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import java.util.List;
import java.util.Optional;

public interface TodoRepositoryPort {

    Todo save(Todo todo);

    Optional<Todo> findByUserIdAndId(Long userId, Long id);

    List<Todo> findAllByUserIdPaged(Long userId, int page, int size);

    List<Todo> findAllByUserIdAndStatusPaged(Long userId, TodoStatus status, int page, int size);

    Long countByUserId(Long userId);

    Long countByUserIdAndStatus(Long userId, TodoStatus status);

    void deleteById(Long id);
}
