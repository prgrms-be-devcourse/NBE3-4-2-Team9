package com.backend.domain.recruitment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.domain.recruitmentUser.dto.request.AuthorRequest;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/initRecruitmentUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecruitmentAuthorTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    long accessExpiration;

    String accessToken1;
    String accessToken2;
    String accessToken3;

    @BeforeAll
    void setUp() {
        SiteUser user1 = userRepository.findByEmail("testEmail1@naver.com").get();
        SiteUser user2 = userRepository.findByEmail("testEmail2@naver.com").get();
        SiteUser user3 = userRepository.findByEmail("testEmail2@naver.com").get();

        CustomUserDetails userDetails1 = new CustomUserDetails(user1);
        CustomUserDetails userDetails2 = new CustomUserDetails(user2);
        CustomUserDetails userDetails3 = new CustomUserDetails(user3);

        accessToken1 = jwtUtil.createAccessToken(userDetails1, accessExpiration);
        accessToken2 = jwtUtil.createAccessToken(userDetails2, accessExpiration);
        accessToken3 = jwtUtil.createAccessToken(userDetails3, accessExpiration);
    }

    @Test
    @DisplayName("새로운 데이터로 모집 지원 승인 테스트")
    void acceptRecruitmentTest_NewData() throws Exception {
        // Given: 게시글 3의 작성자는 user_id=1 (testEmail1@naver.com)
        // 신규 지원 기록: 게시글 3에 대해 지원자 user_id=2(APPLIED) 상태
        Long postId = 3L;
        AuthorRequest request = new AuthorRequest(2L);
        System.out.println("Request JSON: " + objectMapper.writeValueAsString(request));

        // When & Then: 게시글 3의 작성자(토큰: accessToken1, user1)로 지원자 2의 승인을 요청
        mockMvc.perform(patch("/api/v1/recruitment/{postId}/accept", postId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("새로운 데이터로 모집 지원 거절 테스트")
    void rejectRecruitmentTest_NewData() throws Exception {
        // Given: 게시글 3의 작성자는 user_id=1 (testEmail1@naver.com)
        // 신규 지원 기록: 게시글 3에 대해 지원자 user_id=2(APPLIED) 상태
        Long postId = 4L;
        AuthorRequest request = new AuthorRequest(2L);

        // When & Then: 게시글 3의 작성자(토큰: accessToken1)로 지원자 2의 지원 거절 요청
        mockMvc.perform(patch("/api/v1/recruitment/{postId}/reject", postId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 지원자 목록 조회 테스트")
    void getAppliedUsersTest() throws Exception {
        // 예제: 게시글 1의 지원자 목록 조회 (게시글 1의 작성자는 user1)
        Long postId = 1L;
        int pageNum = 0;
        int pageSize = 10;

        mockMvc.perform(get("/api/v1/recruitment/{postId}/applied-users", postId)
                        .header("Authorization", "Bearer " + accessToken1)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.postId").value(postId))
                .andExpect(jsonPath("$.data.recruitmentUserList.content").isArray())
                .andExpect(jsonPath("$.data.recruitmentUserList.pageable.pageNumber").value(pageNum))
                .andExpect(jsonPath("$.data.recruitmentUserList.pageable.pageSize").value(pageSize))
                .andDo(print());
    }

    @Test
    @DisplayName("승인된 참여자 목록 조회 테스트")
    void getAcceptedUsersTest() throws Exception {
        // 예제: 게시글 1의 승인된 참여자 목록 조회 (게시글 1의 작성자는 user1)
        Long postId = 1L;
        int pageNum = 0;
        int pageSize = 10;

        mockMvc.perform(get("/api/v1/recruitment/{postId}/accepted-users", postId)
                        .header("Authorization", "Bearer " + accessToken1)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.postId").value(postId))
                .andExpect(jsonPath("$.data.recruitmentUserList.content").isArray())
                .andExpect(jsonPath("$.data.recruitmentUserList.pageable.pageNumber").value(pageNum))
                .andExpect(jsonPath("$.data.recruitmentUserList.pageable.pageSize").value(pageSize))
                .andDo(print());
    }

    @Test
    @DisplayName("권한 없는 사용자의 모집 지원 승인 시도 테스트")
    void acceptRecruitmentUnauthorizedTest() throws Exception {
        // For 게시글 1, 작성자는 user1.
        // 시나리오: user2 (accessToken2)로 로그인한 상태에서 게시글 1에 대해 지원자 userId=2의 승인을 시도하면 403 Forbidden 발생해야 함.
        Long postId = 1L;
        AuthorRequest request = new AuthorRequest(2L);

        mockMvc.perform(patch("/api/v1/recruitment/{postId}/accept", postId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 대한 모집 지원 승인 시도 테스트")
    void acceptRecruitmentNotFoundTest() throws Exception {
        // 존재하지 않는 게시글 ID 사용 (예: 999)
        Long postId = 999L;
        AuthorRequest request = new AuthorRequest(2L);

        mockMvc.perform(patch("/api/v1/recruitment/{postId}/accept", postId)
                        .header("Authorization", "Bearer " + accessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}