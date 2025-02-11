package com.backend.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.post.dto.PostRequestDto;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository2;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostRepository2 postRepository2;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Test
    @DisplayName("게시글 생성 테스트")
    void testCreatePost() throws Exception {
        //init.sql에 삽입된 유저 데이터 가져오기
        SiteUser writer = userRepository.findByEmail("testEmail1@naver.com").get();
        //가져온 유저 데이터로 시큐리티 유저 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(writer);
        //액세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        PostRequestDto requestDto = PostRequestDto.builder().subject("새로운 제목")
                .content("새로운 내용").categoryId(1L).build();

        mockMvc.perform(post("/api/v1/posts") // 게시글 생성 API 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken) //헤더에 액세스 토큰 삽입
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.subject").value("새로운 제목"))
                .andExpect(jsonPath("$.data.content").value("새로운 내용")).andDo(print());
    }

    @Test
    @DisplayName("로그인한 사용자가 게시글 상세 조회 성공")
    void getPostById_Success() throws Exception {

        //init.sql에 삽입된 유저 데이터 가져오기
        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com").get();
        //가져온 유저 데이터로 시큐리티 유저 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        //액세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        // 게시글 ID 가져오기(ex. 가장 최근 게시글 조회)
        Post testPost = postRepository2.findBySubject("테스트 제목2")
                .orElseThrow(() -> new RuntimeException(
                        "게시글을 찾을 수 없습니다."));// 또는 ID가 작은 순으로 정렬해서 가져올 수도 있음

        Long postId = testPost.getPostId(); // 실제 DB에 존재하는 ID 사용

        mockMvc.perform(get("/api/v1/posts/{id}", postId).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)) // Authorization 헤더 포함
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.subject").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isNotEmpty()).andDo(print());
    }

    @Test
    @DisplayName("로그인 하지 않은 사용자가 게시글 상세 조회")
    void getPostById_Unauthorized() throws Exception {

        Post testPost = postRepository2.findBySubject("테스트 제목2")
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Long postId = testPost.getPostId(); // 실제 DB에 존재하는 ID 사용

        mockMvc.perform(get("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()) //
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void getPostById_NotFound() throws Exception {
        Long notFoundPostNum = 99999L;

        SiteUser writer = userRepository.findByEmail("testEmail3@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CustomUserDetails customUserDetails = new CustomUserDetails(writer);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        mockMvc.perform(get("/api/v1/posts/{id}", notFoundPostNum)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 조건별 조회 테스트")
    void testGetPostById() throws Exception {
        //init.sql에 삽입된 유저 데이터 가져오기
        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com").get();
        //가져온 유저 데이터로 시큐리티 유저 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        //액세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        // 게시글 ID 가져오기(ex. 가장 최근 게시글 조회)
        Post post = postRepository2.findAll().get(0); // 또는 ID가 작은 순으로 정렬해서 가져올 수도 있음
        Long postId = post.getPostId(); // 실제 DB에 존재하는 ID 사용

        mockMvc.perform(get("/api/v1/posts/{id}", postId).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)) // Authorization 헤더 포함
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.subject").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isNotEmpty()).andDo(print());

    }

    @Test
    @DisplayName("게시글 전체 조회 테스트")
    void testGetPosts() throws Exception {
        mockMvc.perform(get("/api/v1/posts")) // JWT 없이 요청
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(jsonPath("$.data").exists()) // 응답 데이터가 존재하는지 확인
                .andExpect(jsonPath("$.data.content").isArray()) // content가 배열인지 확인
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 수정 - 작성자가 게시글 수정 -> 성공")
    void updatePost_Success() throws Exception {
        // 작성자 조회
        SiteUser writer = userRepository.findByEmail("testEmail2@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // jwt 토큰 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(writer);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        // 테스트 할 게시글 조회
        Post testPost = postRepository2.findBySubject("테스트 제목3")
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 수정할 데이터 생성
        PostRequestDto updateRequest = PostRequestDto.builder()
                .subject("수정된 제목")
                .content("수정된 내용")
                .categoryId(testPost.getCategoryId().getId())
                .build();

        // 수정 성공하면 응답 코드가 200 OK, 내용 변경됐는지 확인 필요
        mockMvc.perform(put("/api/v1/posts/{id}", testPost.getPostId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 - 작성자가 아닌 유저가 게시글 수정 -> 실패")
    void updatePost_Forbidden() throws Exception {
        // 작성자가 아닌 유저 조회
        SiteUser otherUser = userRepository.findByEmail("testEmail2@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // jwt 토큰 생성
        CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);
        String accessToken = jwtUtil.createAccessToken(otherUserDetails, ACCESS_EXPIRATION);

        // 테스트 할 게시글 조회
        Post testPost = postRepository2.findBySubject("테스트 제목6")
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 수정할 데이터 생성
        PostRequestDto updateRequest = PostRequestDto.builder()
                .subject("수정된 제목")
                .content("수정된 내용")
                .categoryId(testPost.getCategoryId().getId())
                .build();

        // 수정 실패하고 응답 코드가 403 Or 401, 내용 변경되지 않음
        mockMvc.perform(put("/api/v1/posts/{id}", testPost.getPostId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제 - 작성자가 삭제 -> 성공")
    void testDeletePost_Success() throws Exception {
        SiteUser writer = userRepository.findByEmail("testEmail2@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CustomUserDetails customUserDetails = new CustomUserDetails(writer);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        Post testPost = postRepository2.findBySubject("테스트 제목4")
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        mockMvc.perform(delete("/api/v1/posts/{id}", testPost.getPostId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(print());

        // 삭제 후 존재 여부 확인
        boolean exists = postRepository2.existsById(testPost.getPostId());
        assertThat(exists).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("게시글 삭제 - 작성자가 아닌 유저가 삭제 -> 실패")
    void deletePost_Forbidden() throws Exception {
        Post testPost = postRepository2.findBySubject("테스트 제목5")
                .orElseThrow(() -> new RuntimeException("테스트 게시글을 찾을 수 없습니다."));

        SiteUser otherUser = userRepository.findByEmail("testEmail1@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);
        String accessToken = jwtUtil.createAccessToken(otherUserDetails, ACCESS_EXPIRATION);

        mockMvc.perform(delete("/api/v1/posts/{id}", testPost.getPostId())
                        .header("Authorization", "Bearer " + accessToken)) // 다른 유저의 토큰 사용
                .andExpect(status().isForbidden()) // 403 응답이 나와야 함
                .andDo(print());
    }

    @Test
    @Order(6)
    @DisplayName("게시글 삭제 - 존재하지 않는 게시글 삭제 -> 실패")
    void deletePost_NotFound() throws Exception {
        Long nonExistentPostId = 9999L;

        SiteUser writer = userRepository.findByEmail("testEmail3@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CustomUserDetails customUserDetails = new CustomUserDetails(writer);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        mockMvc.perform(delete("/api/v1/posts/{id}", nonExistentPostId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
