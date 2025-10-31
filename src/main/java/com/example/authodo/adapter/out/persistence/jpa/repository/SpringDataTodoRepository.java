package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTodoRepository extends JpaRepository<TodoJpaEntity, Long> {
    List<TodoJpaEntity> findAllByOrderByIdDesc();
}
