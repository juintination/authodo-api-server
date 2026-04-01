package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTodoRepository extends JpaRepository<TodoJpaEntity, Long> {

    Optional<TodoJpaEntity> findByUserIdAndId(Long userId, Long id);

    List<TodoJpaEntity> findAllByUserIdOrderByIdDesc(Long userId);
}
