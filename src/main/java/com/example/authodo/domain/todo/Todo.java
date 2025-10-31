package com.example.authodo.domain.todo;

import com.example.authodo.domain.todo.enums.TodoStatus;
import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import lombok.*;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 전용 생성자
public class Todo {

    private final Long id;

    private final String title;

    private final String content;

    @Builder.Default
    private final TodoStatus status = TodoStatus.PENDING;

    @Builder.Default
    private final boolean completed = false;

    public static Todo create(String title, String content) {
        validateTitle(title);
        return Todo.builder()
                .id(null)
                .title(title)
                .content(content)
                .status(TodoStatus.PENDING)
                .completed(false)
                .build();
    }

    public Todo change(String newTitle, String newContent, TodoStatus newStatus) {
        String titleToUse = (newTitle != null) ? newTitle : this.title;
        TodoStatus statusToUse = (newStatus != null) ? newStatus : this.status;

        validateTitle(titleToUse);
        validateStatus(statusToUse);

        boolean completedToUse = (statusToUse == TodoStatus.COMPLETED);

        return this.toBuilder()
                .title(titleToUse)
                .content((newContent != null) ? newContent : this.content)
                .status(statusToUse)
                .completed(completedToUse)
                .build();
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.TODO_TITLE_REQUIRED);
        }
    }

    private static void validateStatus(TodoStatus status) {
        if (status == null) {
            throw new BusinessException(ErrorCode.TODO_STATUS_INVALID);
        }
    }

}
