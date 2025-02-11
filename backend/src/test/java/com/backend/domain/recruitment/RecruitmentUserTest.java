package com.backend.domain.recruitment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
public class RecruitmentUserTest {
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
    @DisplayName("모집 신청 테스트")
    @Order(1)
    void applyForRecruitmentTest() throws Exception {
        Long postId = 3L;

        mockMvc.perform(post("/api/v1/recruitment/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 신청 취소 테스트")
    void cancelRecruitmentTest() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/api/v1/recruitment/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }


    @Test
    @DisplayName("모집 신청 상태별 게시판 페이징 조회 테스트")
    @Order(2)
    void getRecruitmentStatusTest() throws Exception {

        mockMvc.perform(get("/api/v1/recruitment/posts")
                .header("Authorization", "Bearer " + accessToken2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("ACCEPTED")) // 상태가 ACCEPTED인지 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.content").isArray())  // content가 배열인지 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.content").isNotEmpty())  // content가 비어 있지 않음을 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.content[0].id").value(1))  // 첫 번째 포스트 ID 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.content[0].subject").value("testSubject"))  // 첫 번째 포스트 제목 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.content[0].content").value("testContent1"))  // 첫 번째 포스트 내용 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.pageable.pageNumber").value(0))  // 페이지 번호 확인
            .andExpect(jsonPath("$.data.postResponseDtoList.pageable.pageSize").value(10))  // 페이지 사이즈 확인
            .andDo(print());
    }

}