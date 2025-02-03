package com.backend.domain.board.service;

import com.backend.domain.board.dto.BoardCreateRequest;
import com.backend.domain.board.dto.BoardResponse;
import com.backend.domain.board.entity.Board;
import com.backend.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    // PostRepository, CategoryRepository, JobPostingRepository 주입
    private final BoardRepository boardRepository;
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    private final CategoryRepository categoryRepository;
//    private final JobPostingRepository jobPostingRepository;

    // 게시글 생성 (DTO 적용)
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @Transactional
//    public PostResponseDto creatPost(PostCreateRequestDto requestDto){
//        // 필수값인 categoryId, jobId 기반 엔티티 조회
//        Category category = categoryRepository.findById(requestDto.getCategoryId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
//
//        // DTO -> Entity 변환
//        Post post = requestDto.toEntity(category, jobPosting);
//
//        // DB 저장
//        Post savedPost = postRepository.save(post);
//
//        return PostResponseDto.fromEntity(savedPost);
//    }

    // 게시글 전체 조회 (DTO 적용)
//    @Transactional(readOnly = true)
//    public Page<PostResponseDto> getAllPosts(Long categoryId, String keyword, String sort, int page,
//            int size) {
//        Pageable pageable;

        // TODO: categoryId가 Null일 때 동작하는지
        // sort 조건이 많아질 수 있다면 enum으로 관리
        // 정렬 방식 설정
//        if ("popular".equals(sort)) {
//            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
//        } else {
//            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        }

        // Repository에서 검색
//        Page<Post> posts;
//        if (categoryId != null) {
//            posts = postRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
//        } else {
//            posts = postRepository.findByKeyword(keyword, pageable);
//        }
//        // Entity -> DTO 변환 후 반환
//        return posts.map(PostResponseDto::fromEntity);
//    }

    // 게시글 상세 조회 (DTO 적용)
    public BoardResponse getPostById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));
        return BoardResponse.fromEntity(board);
    }

    // 게시글 수정 (DTO 적용)
    @Transactional
    public BoardResponse updatePost(Long id, BoardCreateRequest requestDto) {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시물이 존재하지 않습니다. ID: " + id));

        board.updatePost(requestDto.getSubject(), requestDto.getContent());

        return BoardResponse.fromEntity(board); // 게시글 저장
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));
        boardRepository.delete(board);
    }
}
