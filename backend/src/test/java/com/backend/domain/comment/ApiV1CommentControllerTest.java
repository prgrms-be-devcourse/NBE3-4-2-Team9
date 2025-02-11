package com.backend.domain.comment;

import com.backend.domain.comment.dto.request.CommentRequestDto;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiV1CommentControllerTest {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    long accessExpiration;

    Post givenPost;

    SiteUser givenSiteUser1;
    SiteUser givenSiteUser2;

    String accessToken1;
    String accessToken2;

    @BeforeAll
	void setUp() {
        givenPost = postRepository.findById(1L).get();

		givenSiteUser1 = userRepository.findByEmail("testEmail1@naver.com").get();
		CustomUserDetails givenCustomUserDetails1 = new CustomUserDetails(givenSiteUser1);
		accessToken1 = jwtUtil.createAccessToken(givenCustomUserDetails1, accessExpiration);

		givenSiteUser2 = userRepository.findByEmail("testEmail3@naver.com").get();
		CustomUserDetails givenCustomUserDetails2 = new CustomUserDetails(givenSiteUser2);
		accessToken2 = jwtUtil.createAccessToken(givenCustomUserDetails2, accessExpiration);
	}

    @DisplayName("댓글 생성 성공 테스트")
    @Test
    void save_comment_success() throws Exception {
        //given
        CommentRequestDto givenComment = new CommentRequestDto("testContent");
        Long postId = givenPost.getPostId();

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                .header("Authorization", "Bearer " + accessToken1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenComment)));

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value(givenComment.getContent()))
                .andExpect(jsonPath("$.data.id").value(4));
    }

    @DisplayName("댓글 생성시 본문이 비었을 때 실패 테스트")
    @Test
    void save_comment_content_blank_success() throws Exception {
        //given
        CommentRequestDto givenComment = new CommentRequestDto(" ");
        Long postId = givenPost.getPostId();

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                .header("Authorization", "Bearer " + accessToken1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenComment)))
                .andDo(print());

        //then
         resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data[0].field").value("content"))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
    }

    @DisplayName("댓글 수정 성공 테스트")
    @Test
    void modify_comment_success() throws Exception {
        //given
        CommentRequestDto givenComment = new CommentRequestDto("테스트 수정");
        Comment findComment = commentRepository.findById(1L).get();
        Long postId = givenPost.getPostId();

        //when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/posts/{postId}/comments/{id}", postId, findComment.getId())
                        .header("Authorization", "Bearer " + accessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenComment)));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(findComment.getId()))
                .andExpect(jsonPath("$.data.content").value(givenComment.getContent()))
                .andExpect(jsonPath("$.data.createdAt")
                        .value(findComment.getCreatedAt().format(FORMATTER)));
    }

    @DisplayName("댓글 수정시 작성자가 아닐 때 실패 테스트")
    @Test
    void modify_comment_not_author_fail() throws Exception {
        //given
        CommentRequestDto givenComment = new CommentRequestDto("테스트 수정");
        Comment findComment = commentRepository.findById(1L).get();
        Long postId = givenPost.getPostId();

        //when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/posts/{postId}/comments/{id}", postId, findComment.getId())
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenComment)))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.COMMENT_NOT_AUTHOR.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.COMMENT_NOT_AUTHOR.getMessage()));
    }

    @DisplayName("댓글 삭제 성공 테스트")
    @Test
    void delete_comment_success() throws Exception {
        //given
        Long postId = givenPost.getPostId();
        Comment findComment = commentRepository.findById(3L).get();

        //when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/posts/{postId}/comments/{id}", postId, findComment.getId())
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @DisplayName("댓글 삭제시 작성자가 아닐 때 실패 테스트")
    @Test
    void delete_comment_not_author_fail() throws Exception {

        Long postId = givenPost.getPostId();
        Comment findComment = commentRepository.findById(3L).get();

        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/posts/{postId}/comments/{id}", postId,findComment.getId())
                        .header("Authorization", "Bearer " + accessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GlobalErrorCode.COMMENT_NOT_AUTHOR.getCode()))
                .andExpect(jsonPath("$.message").value(GlobalErrorCode.COMMENT_NOT_AUTHOR.getMessage()));

    }

}
