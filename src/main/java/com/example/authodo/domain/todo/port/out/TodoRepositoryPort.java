package com.example.authodo.domain.todo.port.out;

import com.example.authodo.domain.todo.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepositoryPort {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findAll();
    void deleteById(Long id);
}
