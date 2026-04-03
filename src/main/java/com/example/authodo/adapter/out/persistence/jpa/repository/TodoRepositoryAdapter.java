package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.domain.todo.port.out.TodoRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoRepositoryAdapter implements TodoRepositoryPort {

    private final SpringDataTodoRepository springDataTodoRepository;

    private static TodoJpaEntity toEntity(Todo todo) {
        return TodoJpaEntity.builder()
            .id(todo.getId())
            .userId(todo.getUserId())
            .title(todo.getTitle())
            .content(todo.getContent())
            .status(todo.getStatus())
            .completed(todo.isCompleted())
            .build();
    }

    private static Todo toDomain(TodoJpaEntity todoJpaEntity) {
        return Todo.builder()
            .id(todoJpaEntity.getId())
            .userId(todoJpaEntity.getUserId())
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
    public Optional<Todo> findByUserIdAndId(Long userId, Long id) {
        return springDataTodoRepository.findByUserIdAndId(userId, id).map(TodoRepositoryAdapter::toDomain);
    }

    @Override
    public List<Todo> findAllByUserIdPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return springDataTodoRepository.findAllByUserId(userId, pageable).stream()
            .map(TodoRepositoryAdapter::toDomain)
            .toList();
    }

    @Override
    public List<Todo> findAllByUserIdAndStatusPaged(Long userId, TodoStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return springDataTodoRepository.findAllByUserIdAndStatus(userId, status, pageable).stream()
            .map(TodoRepositoryAdapter::toDomain)
            .toList();
    }

    @Override
    public Long countByUserId(Long userId) {
        return springDataTodoRepository.countByUserId(userId);
    }

    @Override
    public Long countByUserIdAndStatus(Long userId, TodoStatus status) {
        return springDataTodoRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public void deleteById(Long id) {
        springDataTodoRepository.deleteById(id);
    }

}
