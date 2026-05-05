package com.example.authodo.adapter.in.web.auth;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.LoginRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.RefreshTokenRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.SignupRequest;
import com.example.authodo.config.RedisContainerConfig;
import com.example.authodo.config.WebRestDocsTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@WebRestDocsTest
@Import(RedisContainerConfig.class)
@DisplayName("AuthController 통합 테스트")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/signup - 회원가입")
    void signup_success() throws Exception {
        SignupRequest request = new SignupRequest("test@email.com", "password123", "nickname");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("auth-signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                ),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("POST /api/auth/login - 로그인")
    void login_success() throws Exception {
        signupTestUser();

        LoginRequest request = new LoginRequest("test@email.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("auth-login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("GET /api/auth/me - 내 정보 조회")
    void getMyInfo_success() throws Exception {
        signupTestUser();
        String token = loginAndReturnAccessToken();

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andDo(document("auth-me",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - 토큰 재발급")
    void refresh_success() throws Exception {
        signupTestUser();

        String refreshToken = loginAndReturnRefreshToken();

        RefreshTokenRequest req = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(document("auth-refresh",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("refreshToken")
                        .type(JsonFieldType.STRING)
                        .description("리프레시 토큰")
                ),
                responseFields(
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("새 액세스 토큰"),
                    fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("새 리프레시 토큰"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional().description("결과 메시지")
                )
            ));
    }

    private void signupTestUser() throws Exception {
        SignupRequest request = new SignupRequest("test@email.com", "password123", "nickname");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    private String loginAndReturnAccessToken() throws Exception {
        LoginRequest request = new LoginRequest("test@email.com", "password123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("data").get("accessToken").asText();
    }

    private String loginAndReturnRefreshToken() throws Exception {
        LoginRequest request = new LoginRequest("test@email.com", "password123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("data").get("refreshToken").asText();
    }
}
