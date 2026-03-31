package com.example.authodo.adapter.in.web.todo;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoCreateRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoUpdateRequest;
import com.example.authodo.application.todo.TodoService;
import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.config.WebRestDocsTest;
import com.example.authodo.domain.todo.Todo;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebRestDocsTest
@WebMvcTest(TodoController.class)
@DisplayName("TodoController 테스트")
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private TodoService todoService;

    private Todo todo(Long id, String title, String content, TodoStatus status) {
        LocalDateTime now = LocalDateTime.of(2025, 3, 12, 0, 0, 0);
        return Todo.builder()
            .id(id)
            .title(title)
            .content(content)
            .status(status)
            .completed(status == TodoStatus.COMPLETED)
            .createdAt(now)
            .modifiedAt(now)
            .build();
    }

    @Test
    @DisplayName("POST /api/todos - 생성 성공")
    void createTodo_success() throws Exception {
        TodoCreateRequest req = new TodoCreateRequest("RestDocs 학습", "API 문서 자동화");
        Todo saved = todo(1L, req.title(), req.content(), TodoStatus.PENDING);
        given(todoService.create(req.title(), req.content())).willReturn(saved);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(document("todo-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("할 일 제목"),
                    fieldWithPath("content").type(JsonFieldType.STRING).optional().description("할 일 내용")
                ),
                responseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 Todo ID"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("POST /api/todos - 유효성 실패(제목 없음)")
    void createTodo_validation_fail() throws Exception {
        TodoCreateRequest bad = new TodoCreateRequest(" ", "내용");
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bad)))
            .andExpect(status().isBadRequest())
            .andDo(document("todo-create-400",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            ));
    }

    @Test
    @DisplayName("GET /api/todos/{id} - 조회 성공")
    void getTodo_success() throws Exception {
        Todo data = todo(1L, "제목", "내용", TodoStatus.PENDING);
        given(todoService.get(1L)).willReturn(data);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos/{id}", 1L))
            .andExpect(status().isOk())
            .andDo(document("todo-get",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("Todo ID")
                ),
                responseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).optional().description("내용"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING)
                        .description("상태(PENDING/IN_PROGRESS/COMPLETED)"),
                    fieldWithPath("data.completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).optional().description("생성시각(ISO-8601)"),
                    fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).optional()
                        .description("수정시각(ISO-8601)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos/{id} - 조회 실패(NOT_FOUND)")
    void getTodo_notFound() throws Exception {
        given(todoService.get(0L)).willThrow(new BusinessException(ErrorCode.TODO_NOT_FOUND, 0L));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos/{id}", 0L))
            .andExpect(status().isNotFound())
            .andDo(document("todo-get-404",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("존재하지 않는 ID")
                ),
                responseFields(
                    fieldWithPath("data.timestamp").type(JsonFieldType.STRING).description("오류 시각"),
                    fieldWithPath("data.path").type(JsonFieldType.STRING).description("요청 경로"),
                    fieldWithPath("data.code").type(JsonFieldType.STRING).description("에러코드"),
                    fieldWithPath("data.message").type(JsonFieldType.STRING).optional().description("메시지(환경에 따라)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("표시 메시지")
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos - 목록 조회 성공")
    void getAll_success() throws Exception {
        List<Todo> list = List.of(
            todo(2L, "두번째", "내용2", TodoStatus.PENDING),
            todo(1L, "첫번째", "내용1", TodoStatus.COMPLETED)
        );
        given(todoService.getAll()).willReturn(list);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos"))
            .andExpect(status().isOk())
            .andDo(document("todo-getAll",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).optional().description("내용"),
                    fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태"),
                    fieldWithPath("data[].completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).optional()
                        .description("생성시각(ISO-8601)"),
                    fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).optional()
                        .description("수정시각(ISO-8601)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - 수정 성공")
    void update_success() throws Exception {
        TodoUpdateRequest req =
            new TodoUpdateRequest("수정된 제목", "수정된 내용", TodoStatus.COMPLETED);

        doNothing().when(todoService).update(1L, req.title(), req.content(), req.status());

        Todo after = todo(1L, req.title(), req.content(), TodoStatus.COMPLETED);
        given(todoService.get(1L)).willReturn(after);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/todos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(document("todo-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("수정할 ID")),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).optional().description("새 제목"),
                    fieldWithPath("content").type(JsonFieldType.STRING).optional().description("새 내용"),
                    fieldWithPath("status").type(JsonFieldType.STRING).optional().description("새 상태")
                ),
                responseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).optional().description("내용"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태"),
                    fieldWithPath("data.completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).optional().description("생성시각(ISO-8601)"),
                    fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).optional()
                        .description("수정시각(ISO-8601)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                )
            ));

        verify(todoService).update(1L, req.title(), req.content(), req.status());
        verify(todoService).get(1L);
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - 수정 실패(NOT_FOUND)")
    void update_notFound() throws Exception {
        TodoUpdateRequest req = new TodoUpdateRequest("수정", "내용", TodoStatus.PENDING);
        willThrow(new BusinessException(ErrorCode.TODO_NOT_FOUND, 404L))
            .given(todoService).update(404L, req.title(), req.content(), req.status());

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/todos/{id}", 404L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isNotFound())
            .andDo(document("todo-update-404",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("존재하지 않는 ID"))
            ));
    }

    // ===== delete =====
    @Test
    @DisplayName("DELETE /api/todos/{id} - 삭제 성공")
    void delete_success() throws Exception {
        willDoNothing().given(todoService).delete(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/todos/{id}", 1L))
            .andExpect(status().isOk())
            .andDo(document("todo-delete",
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("삭제할 ID")),
                responseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("삭제된 ID"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} - 삭제 실패(NOT_FOUND)")
    void delete_notFound() throws Exception {
        willThrow(new BusinessException(ErrorCode.TODO_NOT_FOUND, 0L))
            .given(todoService).delete(0L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/todos/{id}", 0L))
            .andExpect(status().isNotFound())
            .andDo(document("todo-delete-404",
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("존재하지 않는 ID"))
            ));
    }

}
