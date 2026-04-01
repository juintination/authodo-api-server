package com.example.authodo.domain.todo;

import com.example.authodo.domain.todo.enums.TodoStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Todo {

    private final Long id;

    private final Long userId;

    private final String title;

    private final String content;

    @Builder.Default
    private final TodoStatus status = TodoStatus.PENDING;

    @Builder.Default
    private final boolean completed = false;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public static Todo create(Long userId, String title, String content) {
        return Todo.builder()
            .id(null)
            .userId(userId)
            .title(title)
            .content(content)
            .status(TodoStatus.PENDING)
            .completed(false)
            .build();
    }

    public Todo change(String newTitle, String newContent, TodoStatus newStatus) {
        String title = (newTitle != null) ? newTitle : this.title;
        TodoStatus status = (newStatus != null) ? newStatus : this.status;

        boolean completedToUse = (status == TodoStatus.COMPLETED);

        return this.toBuilder()
            .title(title)
            .content((newContent != null) ? newContent : this.content)
            .status(status)
            .completed(completedToUse)
            .build();
    }

}
