package com.example.authodo.application.todo;

import com.example.authodo.adapter.out.persistence.jpa.entity.TodoJpaEntity;
import com.example.authodo.adapter.out.persistence.jpa.repository.SpringDataTodoRepository;
import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.domain.todo.enums.TodoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final SpringDataTodoRepository springDataTodoRepository;

    @Transactional
    public Long create(String title, String content) {
        TodoJpaEntity todoJpaEntity = TodoJpaEntity.builder()
                .title(title)
                .content(content)
                .status(TodoStatus.PENDING)
                .completed(false)
                .build();
        return springDataTodoRepository.save(todoJpaEntity).getId();
    }

    public TodoJpaEntity get(Long id) {
        return springDataTodoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND, id));
    }

    public List<TodoJpaEntity> getAll() {
        return springDataTodoRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public void update(Long id, String title, String content, TodoStatus status) {
        TodoJpaEntity todoJpaEntity = get(id);
        if (title != null) {
            todoJpaEntity.changeTitle(title);
        }
        if (content != null) {
            todoJpaEntity.changeContent(content);
        }
        if (status != null) {
            todoJpaEntity.changeStatus(status);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!springDataTodoRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.TODO_NOT_FOUND, id);
        }
        springDataTodoRepository.deleteById(id);
    }

}
