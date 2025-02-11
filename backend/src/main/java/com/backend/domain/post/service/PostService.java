package com.backend.domain.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.post.dto.PostRequestDto;
import com.backend.domain.post.dto.PostResponseDto;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final JobPostingRepository jobPostingRepository;


    //  게시글 생성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, SiteUser user) {

        // 1. 글 작성 전 카테고리 먼저 조회
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

        JobPosting jobPosting = null;

        // 모집 게시판일 때 jobpostingId가 필수
        if ("모집 게시판".equals(category.getName())) {
            if (requestDto.getJobPostingId() == null) {
                throw new GlobalException(GlobalErrorCode.JOB_POSTING_REQUIRED);
            }
            // JobPostingRepository를 이용하여 채용 공고 조회
            jobPosting = jobPostingRepository.findById(requestDto.getJobPostingId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.JOB_POSTING_REQUIRED));
        }

        // DTO -> Entity 변환
        Post post = Post.builder().subject(requestDto.getSubject()).content(requestDto.getContent())
                .categoryId(category).jobId(jobPosting)
                .recruitmentStatus(jobPosting != null ? RecruitmentStatus.OPEN : null).author(user)
                .build();

        // DB 저장
        Post savedPost = postRepository.save(post);
        return savedPost.toDto(user.getId());
    }

    // 게시글 전체 조회 (postType → categoryId 변경)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Long categoryId, String keyword, String sort, int page,
            int size, Long currentUserId) {
        Pageable pageable;

        // 정렬 방식 설정 (viewCount 또는 createdAt)
        if ("popular".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // Repository에서 검색
        Page<Post> posts;
        if ((keyword == null || keyword.trim().isEmpty()) && categoryId == null) {
            // 전체 게시글 조회
            posts = postRepository.findAll(pageable);
        } else if (categoryId != null) {
            // 카테고리 & 검색어 필터링
            posts = postRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
        } else {
            // 키워드 필터링
            posts = postRepository.findByKeyword(keyword, pageable);
        }

        return posts.map(post -> post.toDto(currentUserId));

    }

    //  게시글 상세 조회
    public PostResponseDto getPostById(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
        return post.toDto(currentUserId);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, long userId) {

        Post post = postRepository.findById(id).orElseThrow(
            () -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        log.info("삭제 요청한 사용자 ID: {}", userId);
        log.info("게시글 작성자 ID: {}", post.getAuthor().getId());

        if (!post.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.POST_DELETE_FORBIDDEN);
        }
        postRepository.delete(post);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, long userId) {

        // 게시글 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        // 작성자 검증
        if (!post.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.POST_UPDATE_FORBIDDEN);
        }
        // 게시글
        post.updatePost(
                requestDto.getSubject(),
                requestDto.getContent(),
                requestDto.getRecruitmentClosingDate(),
                requestDto.getNumOfApplicants()
        );
        return post.toDto(userId); // 게시글 저장

    }

}
