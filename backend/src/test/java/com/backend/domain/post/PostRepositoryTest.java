package com.backend.domain.post;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    private Category freeBoardCategory;
    private Category recruitmentBoardCategory;

    @Autowired
    private UserRepository userRepository;

     @Autowired
     private RecruitmentUserRepository recruitmentUserRepository;

    private SiteUser testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findByEmail("testEmail1@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 카테고리 가져오기
        Optional<Category> freeCategories = categoryRepository.findByName("자유 게시판");
        if (freeCategories.isEmpty()) {
            throw new RuntimeException("자유 게시판 카테고리를 찾을 수 없습니다.");
        }
        freeBoardCategory = freeCategories.get();

        Optional<Category> recruitmentCategories = categoryRepository.findByName("모집 게시판");
        if (recruitmentCategories.isEmpty()) {
            throw new RuntimeException("모집 게시판을 찾을 수 없습니다.");
        }
        recruitmentBoardCategory = recruitmentCategories.get();

        // 테스트 데이터 가져오기
        testPost = postRepository.findBySubject("testSubject")
                .orElseThrow(() -> new RuntimeException("테스트 게시글을 찾을 수 없습니다."));

        // recruitmentUserRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 조회 - 카테고리 기준으로 게시글 조회")
    void testFindByCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유 게시판 조회
        Page<Post> freePosts = postRepository.findAllByCategoryId(freeBoardCategory.getId(),
                pageable);
        assertThat(freePosts.getContent()).hasSize(3);

        // 모집 게시판 조회
        Page<Post> recruitmentPosts = postRepository.findAllByCategoryId(
                recruitmentBoardCategory.getId(), pageable);
        assertThat(recruitmentPosts.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("게시글 조회 - 제목 or 내용으로 검색")
    void testFindByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // "test" 키워드가 포함된 모든 게시글 검색 (자유게시판, 모집게시판 모두 포함)
        Page<Post> posts = postRepository.findByKeyword("test", pageable);
        assertThat(posts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("게시글 조회 - 카테고리와 검색어를 기반으로 게시글 조회")
    void testFindByCategoryAndKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유게시판에서 "test" 포함된 게시글 검색
        Page<Post> posts_free1 = postRepository.findByCategoryAndKeyword(freeBoardCategory.getId(),
                "test",
                pageable);
        assertThat(posts_free1.getContent()).hasSize(1);

        // 자유게시판에서 "테스트" 포함된 게시글 검색 -> 없음
        Page<Post> posts_free2 = postRepository.findByCategoryAndKeyword(freeBoardCategory.getId(),
                "없음",
                pageable);
        assertThat(posts_free2.getContent()).hasSize(0);

        // 모집게시판에서 "테스트" 포함된 게시글 검색
        Page<Post> posts_recruitment1 = postRepository.findByCategoryAndKeyword(
                recruitmentBoardCategory.getId(), "테스트",
                pageable);
        assertThat(posts_recruitment1.getContent()).hasSize(3);

        // 모집게시판에서 "test" 포함된 게시글 검색 -> 없음
        Page<Post> posts_recruitment2 = postRepository.findByCategoryAndKeyword(
                recruitmentBoardCategory.getId(), "test",
                pageable);
        assertThat(posts_recruitment2.getContent()).hasSize(0);

    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회시 빈값 반환")
    void findById_NotFound() {
        Optional<Post> foundPost = postRepository.findById(9999L);
        assertThat(foundPost).isEmpty();
    }

    @Test
    @DisplayName("게시글 저장 및 조회 테스트")
    void testSaveAndFindPost() {
        // given
        Post post = Post.builder()
                .subject("테스트 제목")
                .content("테스트 내용")
                .author(testUser)
                .categoryId(freeBoardCategory)
                .build();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost.getPostId()).isNotNull();
        assertThat(savedPost.getSubject()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("게시글 삭제 - 게시글 삭제 후 조회 시 존재하지 않음")
    void testDeletePost_Success() {
        // 게시글 삭제
        postRepository.deleteById(testPost.getPostId());

        // 삭제한 게시글 조회 -> 존재하지 않아야 함
        boolean exists = postRepository.existsById(testPost.getPostId());
        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 게시글 삭제 시 예외 발생")
    void testDeletePost_NotFound() {
        Long nonExistentId = 9999L;
        assertThrows(RuntimeException.class, () -> {
            Post post = postRepository.findById(nonExistentId)
                    .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
            postRepository.delete(post);
        });
    }

    @Test
    @DisplayName("게시글 수정 - 수정 후 조회")
    void testUpdatePost_Success() {

        String updatedTitle = "수정된 제목";
        String updatedContent = "수정된 내용";

        testPost.updatePost(updatedTitle, updatedContent);
        postRepository.save(testPost);

        Optional<Post> updatedPost = postRepository.findById(testPost.getPostId());

        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getSubject()).isEqualTo(updatedTitle);
        assertThat(updatedPost.get().getContent()).isEqualTo(updatedContent);
    }

    // findExpiredRecruitmentPosts()

    // findAllByCategoryId()

    // findByKeyword()

    // findByRecruitmentStatus()
}

