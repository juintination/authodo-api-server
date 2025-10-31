package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.port.TodoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TodoRepositoryAdapter implements TodoRepositoryPort {

    private final SpringDataTodoRepository springDataTodoRepository;

    private static TodoJpaEntity toEntity(Todo todo) {
        return TodoJpaEntity.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .content(todo.getContent())
                .status(todo.getStatus())
                .completed(todo.isCompleted())
                .build();
    }

    private static Todo toDomain(TodoJpaEntity todoJpaEntity) {
        return Todo.builder()
                .id(todoJpaEntity.getId())
                .title(todoJpaEntity.getTitle())
                .content(todoJpaEntity.getContent())
                .status(todoJpaEntity.getStatus())
                .completed(todoJpaEntity.isCompleted())
                .createdAt(todoJpaEntity.getCreatedAt())
                .modifiedAt(todoJpaEntity.getModifiedAt())
                .build();
    }

    @Override
    public Todo save(Todo todo) {
        TodoJpaEntity saved = springDataTodoRepository.save(toEntity(todo));
        return toDomain(saved);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return springDataTodoRepository.findById(id).map(TodoRepositoryAdapter::toDomain);
    }

    @Override
    public List<Todo> findAll() {
        return springDataTodoRepository.findAllByOrderByIdDesc().stream()
                .map(TodoRepositoryAdapter::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        springDataTodoRepository.deleteById(id);
    }

}
