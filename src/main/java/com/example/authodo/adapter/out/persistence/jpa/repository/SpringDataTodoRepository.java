package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.domain.todo.enums.TodoStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTodoRepository extends JpaRepository<TodoJpaEntity, Long> {

    Optional<TodoJpaEntity> findByUserIdAndId(Long userId, Long id);

    List<TodoJpaEntity> findAllByUserId(Long userId, Pageable pageable);

    List<TodoJpaEntity> findAllByUserIdAndStatus(Long userId, TodoStatus status, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, TodoStatus status);
}
