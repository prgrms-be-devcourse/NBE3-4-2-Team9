package com.backend.domain.recruitment;

import static org.junit.jupiter.api.Assertions.*;
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

import com.backend.domain.post.entity.RecruitmentPost;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.post.PostRepository;
import com.backend.domain.post.repository.recruitment.RecruitmentPostRepository;
import com.backend.domain.recruitmentUser.dto.request.AuthorRequest;
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
    PostRepository postRepository;

    @Autowired
    RecruitmentPostRepository recruitmentPostRepository;

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
        SiteUser user3 = userRepository.findByEmail("testEmail3@naver.com").get();

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

        mockMvc.perform(get("/api/v1/recruitment/accepted-posts")
                .header("Authorization", "Bearer " + accessToken2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("ACCEPTED")) // 상태가 ACCEPTED인지 확인
            .andExpect(jsonPath("$.data.postPageResponses.content").isArray())  // content가 배열인지 확인
            .andExpect(jsonPath("$.data.postPageResponses.content").isNotEmpty())  // content가 비어 있지 않음을 확인
            .andExpect(jsonPath("$.data.postPageResponses.content[0].postId").value(4))  // 첫 번째 포스트 ID 확인
            .andExpect(jsonPath("$.data.postPageResponses.content[0].subject").value("테스트 제목4"))  // 첫 번째 포스트 제목 확인
            .andExpect(jsonPath("$.data.postPageResponses.content[0].authorName").value("testName2"))  // 첫 번째 포스트 작성자 이름 확인
            .andExpect(jsonPath("$.data.postPageResponses.pageable.pageNumber").value(0))  // 페이지 번호 확인
            .andExpect(jsonPath("$.data.postPageResponses.pageable.pageSize").value(10))  // 페이지 사이즈 확인
            .andDo(print());
    }



    @Test
    @DisplayName("모집 지원 승인 후 모집 상태가 CLOSED로 변경되는지 테스트")
    void testAcceptRecruitmentClosedStatus() throws Exception {
        // 가정: 테스트 데이터에 postId=6인 게시글이 존재하며,
        //       해당 게시글은 최대 모집 인원(num_of_applicants)이 1이고,
        //       초기 current_user_count가 0 또는 1인 상태이며,
        //       recruitment_status는 null(또는 OPEN) 상태로 저장되어 있음.
        Long postId = 6L;

        // 승인 요청할 지원자의 userId를 AuthorRequest로 생성 (예: 지원자 id=2)
        AuthorRequest request = new AuthorRequest(2L);

        // 게시글 작성자(권한 있는 사용자)의 JWT 토큰(accessToken1)을 사용하여 승인 요청 API 호출
        mockMvc.perform(patch("/api/v1/recruitment/{postId}/accept", postId)
                        .header("Authorization", "Bearer " + accessToken3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // API 호출 후, DB에서 해당 게시글을 다시 조회하여 모집 상태가 CLOSED로 업데이트되었는지 확인
        RecruitmentPost updatedPost = recruitmentPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 만약 current_user_count가 최대 모집 인원 이상이면, 내부 로직에 따라 상태가 CLOSED여야 합니다.
        assertEquals(RecruitmentStatus.CLOSED, updatedPost.getRecruitmentStatus(),
                "모집 지원 승인이 완료되면 모집 상태는 CLOSED여야 합니다.");

        System.out.println("최종 모집 상태: " + updatedPost.getRecruitmentStatus());
    }

    @Test
    @DisplayName("인원이 다 차있지 않으면 OPEN으로 유지되는지 테스트")
    void testRecruitmentStatusRemainsOpen() throws Exception {
        Long postId = 5L;

        AuthorRequest request = new AuthorRequest(3L);

        mockMvc.perform(patch("/api/v1/recruitment/{postId}/accept", postId)
                        .header("Authorization", "Bearer " + accessToken3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // 승인 요청 후, DB에서 해당 게시글을 다시 조회
        RecruitmentPost updatedPost = recruitmentPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        assertEquals(RecruitmentStatus.OPEN, updatedPost.getRecruitmentStatus(),
                "모집 인원이 다 차지 않으면 모집 상태는 OPEN이어야 합니다.");

        System.out.println("최종 모집 상태: " + updatedPost.getRecruitmentStatus());
    }
}