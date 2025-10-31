package com.example.authodo.adapter.out.persistence.jpa.entity;

import com.example.authodo.adapter.out.persistence.jpa.entity.base.TimeStampedEntity;
import com.example.authodo.domain.todo.enums.TodoStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "todos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TodoJpaEntity extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private TodoStatus status = TodoStatus.PENDING;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

}
