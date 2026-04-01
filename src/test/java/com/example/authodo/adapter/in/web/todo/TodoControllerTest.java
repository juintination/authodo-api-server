package com.example.authodo.adapter.in.web.todo;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoCreateRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoUpdateRequest;
import com.example.authodo.config.WebRestDocsTest;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@WebRestDocsTest
@DisplayName("TodoController 통합 테스트")
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/todos - 생성")
    void createTodo_success() throws Exception {
        TodoCreateRequest req = new TodoCreateRequest("RestDocs 학습", "API 문서 자동화");

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
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data").type(JsonFieldType.NUMBER).description("생성된 Todo ID"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos/{id} - 조회")
    void getTodo_success() throws Exception {
        Long id = createTodoAndReturnId();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos/{id}", id))
            .andExpect(status().isOk())
            .andDo(document("todo-get",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("Todo ID")
                ),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).optional().description("내용"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING)
                        .description("상태(PENDING/IN_PROGRESS/COMPLETED)"),
                    fieldWithPath("data.completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).optional().description("생성시각(ISO-8601)"),
                    fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).optional()
                        .description("수정시각(ISO-8601)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos - 목록 조회")
    void getAll_success() throws Exception {
        createTodoAndReturnId();
        createTodoAndReturnId();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos"))
            .andExpect(status().isOk())
            .andDo(document("todo-getAll",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).optional().description("내용"),
                    fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태"),
                    fieldWithPath("data[].completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).optional()
                        .description("생성시각(ISO-8601)"),
                    fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).optional()
                        .description("수정시각(ISO-8601)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - 수정")
    void update_success() throws Exception {
        Long id = createTodoAndReturnId();

        TodoUpdateRequest req =
            new TodoUpdateRequest("수정된 제목", "수정된 내용", TodoStatus.COMPLETED);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/todos/{id}", id)
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
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} - 삭제")
    void delete_success() throws Exception {
        Long id = createTodoAndReturnId();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/todos/{id}", id))
            .andExpect(status().isOk())
            .andDo(document("todo-delete",
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("삭제할 ID")),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    private Long createTodoAndReturnId() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest("제목", "내용");

        String response = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("data").asLong();
    }
}
