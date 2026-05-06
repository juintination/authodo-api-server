package com.example.authodo.adapter.in.web.todo;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.authodo.adapter.in.web.security.util.SecurityUtil;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoCreateRequest;
import com.example.authodo.adapter.in.web.todo.dto.TodoDtos.TodoUpdateRequest;
import com.example.authodo.application.todo.dto.command.CreateTodoCommand;
import com.example.authodo.application.todo.usecase.create.CreateTodoUseCase;
import com.example.authodo.config.WebRestDocsTest;
import com.example.authodo.domain.todo.enums.TodoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebRestDocsTest
@WithMockUser(username = "1")
@DisplayName("TodoController 통합 테스트")
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CreateTodoUseCase createTodoUseCase;

    @Autowired
    SecurityUtil securityUtil;

    @Test
    @DisplayName("POST /api/todos - 생성")
    void createTodo_success() throws Exception {
        TodoCreateRequest req = new TodoCreateRequest("RestDocs 학습", "API 문서 자동화");

        FieldDescriptor[] reqFields = {
            fieldWithPath("title").type(JsonFieldType.STRING).description("할 일 제목"),
            fieldWithPath("content").type(JsonFieldType.STRING).optional().description("할 일 내용")
        };
        FieldDescriptor[] resFields = {
            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
            fieldWithPath("data").type(JsonFieldType.NUMBER).description("생성된 Todo ID"),
            fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(document("todo-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(reqFields),
                responseFields(resFields),
                resource(ResourceSnippetParameters.builder()
                    .tag("Todo")
                    .summary("Todo 생성")
                    .requestFields(reqFields)
                    .responseFields(resFields)
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos/{todoId} - 조회")
    void getTodo_success() throws Exception {
        Long id = createTodoAndReturnId();

        FieldDescriptor[] resFields = {
            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
            fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
            fieldWithPath("data.content").type(JsonFieldType.STRING).optional().description("내용"),
            fieldWithPath("data.status").type(JsonFieldType.STRING)
                .description("상태 (PENDING / IN_PROGRESS / COMPLETED)"),
            fieldWithPath("data.completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).optional()
                .description("생성시각 (ISO-8601)"),
            fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).optional()
                .description("수정시각 (ISO-8601)"),
            fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos/{todoId}", id))
            .andExpect(status().isOk())
            .andDo(document("todo-get",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("todoId").description("Todo ID")
                ),
                responseFields(resFields),
                resource(ResourceSnippetParameters.builder()
                    .tag("Todo")
                    .summary("Todo 단건 조회")
                    .pathParameters(
                        ResourceDocumentation.parameterWithName("todoId").description("Todo ID")
                    )
                    .responseFields(resFields)
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("GET /api/todos - 목록 조회")
    void getAll_success() throws Exception {
        int todoCount = 2;
        for (int i = 0; i < todoCount; i++) {
            createTodoAndReturnId();
        }

        FieldDescriptor[] resFields = {
            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
            fieldWithPath("data.items[].id").type(JsonFieldType.NUMBER).description("ID"),
            fieldWithPath("data.items[].title").type(JsonFieldType.STRING).description("제목"),
            fieldWithPath("data.items[].content").type(JsonFieldType.STRING).optional().description("내용"),
            fieldWithPath("data.items[].status").type(JsonFieldType.STRING).description("상태"),
            fieldWithPath("data.items[].completed").type(JsonFieldType.BOOLEAN).description("완료 여부"),
            fieldWithPath("data.items[].createdAt").type(JsonFieldType.STRING).optional()
                .description("생성시각 (ISO-8601)"),
            fieldWithPath("data.items[].modifiedAt").type(JsonFieldType.STRING).optional()
                .description("수정시각 (ISO-8601)"),
            fieldWithPath("data.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
            fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("전체 데이터 수"),
            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
            fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/todos")
                .param("page", "1")
                .param("size", Long.toString(todoCount))
            )
            .andExpect(status().isOk())
            .andDo(document("todo-getAll",
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("size").description("페이지 크기"),
                    parameterWithName("status").optional().description("상태 필터 (PENDING / IN_PROGRESS / COMPLETED)")
                ),
                responseFields(resFields),
                resource(ResourceSnippetParameters.builder()
                    .tag("Todo")
                    .summary("Todo 목록 조회")
                    .queryParameters(
                        ResourceDocumentation.parameterWithName("page").description("페이지 번호"),
                        ResourceDocumentation.parameterWithName("size").description("페이지 크기"),
                        ResourceDocumentation.parameterWithName("status").optional().description("상태 필터 (PENDING / IN_PROGRESS / COMPLETED)")
                    )
                    .responseFields(resFields)
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("PUT /api/todos/{todoId} - 수정")
    void update_success() throws Exception {
        Long id = createTodoAndReturnId();

        TodoUpdateRequest req = new TodoUpdateRequest("수정된 제목", "수정된 내용", TodoStatus.COMPLETED);

        FieldDescriptor[] reqFields = {
            fieldWithPath("title").type(JsonFieldType.STRING).optional().description("새 제목"),
            fieldWithPath("content").type(JsonFieldType.STRING).optional().description("새 내용"),
            fieldWithPath("status").type(JsonFieldType.STRING).optional()
                .description("새 상태 (PENDING / IN_PROGRESS / COMPLETED)")
        };
        FieldDescriptor[] resFields = {
            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
            fieldWithPath("data").type(JsonFieldType.NULL).optional().description("반환 데이터 없음"),
            fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/todos/{todoId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(document("todo-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("todoId").description("수정할 Todo ID")
                ),
                requestFields(reqFields),
                responseFields(resFields),
                resource(ResourceSnippetParameters.builder()
                    .tag("Todo")
                    .summary("Todo 수정")
                    .pathParameters(
                        ResourceDocumentation.parameterWithName("todoId").description("수정할 Todo ID")
                    )
                    .requestFields(reqFields)
                    .responseFields(resFields)
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("DELETE /api/todos/{todoId} - 삭제")
    void delete_success() throws Exception {
        Long id = createTodoAndReturnId();

        FieldDescriptor[] resFields = {
            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
            fieldWithPath("data").type(JsonFieldType.NULL).optional().description("반환 데이터 없음"),
            fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/todos/{todoId}", id))
            .andExpect(status().isOk())
            .andDo(document("todo-delete",
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("todoId").description("삭제할 Todo ID")
                ),
                responseFields(resFields),
                resource(ResourceSnippetParameters.builder()
                    .tag("Todo")
                    .summary("Todo 삭제")
                    .pathParameters(
                        ResourceDocumentation.parameterWithName("todoId").description("삭제할 Todo ID")
                    )
                    .responseFields(resFields)
                    .build()
                )
            ));
    }

    private Long createTodoAndReturnId() {
        return createTodoUseCase.create(securityUtil.getCurrentUserId(), new CreateTodoCommand("제목", "내용")).getId();
    }
}
