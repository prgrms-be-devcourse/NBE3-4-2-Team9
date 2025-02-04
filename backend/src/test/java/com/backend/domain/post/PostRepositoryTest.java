package com.backend.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category freeBoardCategory;
    private Category recruitmentBoardCategory;

    @BeforeEach
    void setUp() {
        freeBoardCategory = categoryRepository.save(Category.builder().name("자유게시판").build());
        recruitmentBoardCategory = categoryRepository.save(Category.builder().name("모집게시판").build());

        // 자유게시판 게시글 저장
        postRepository.save(new Post("자유게시판 글1", "내용1", freeBoardCategory));
        postRepository.save(new Post("자유게시판 글2", "내용2", freeBoardCategory));

        // 모집게시판 게시글 저장
        postRepository.save(new Post("모집글1", "백엔드 개발자 모집", recruitmentBoardCategory));
    }

    @Test
    @DisplayName("카테고리 기준으로 게시글 조회")
    void testFindByCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유게시판 조회
        Page<Post> freePosts = postRepository.findAllByCategoryId(freeBoardCategory.getId(),
                pageable);
        assertThat(freePosts.getContent()).hasSize(2);

        // 모집게시판 조회
        Page<Post> recruitmentPosts = postRepository.findAllByCategoryId(
                recruitmentBoardCategory.getId(), pageable);
        assertThat(recruitmentPosts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("제목 or 내용으로 검색")
    void testFindByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // "모집" 키워드 검색 (모집 게시판 글만 검색)
        Page<Post> posts = postRepository.findByKeyword("모집", pageable);
        assertThat(posts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("카테고리와 검색어를 기반으로 게시글 조회")
    void testFindByCategoryAndKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유게시판에서 "글" 포함된 게시글 검색
        Page<Post> posts = postRepository.findByCategoryAndKeyword(freeBoardCategory.getId(), "글",
                pageable);
        assertThat(posts.getContent()).hasSize(2);
    }
}