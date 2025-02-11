package com.backend.domain.post.controller;

import com.backend.domain.post.dto.PostRequestDto;
import com.backend.domain.post.dto.PostResponseDto;
import com.backend.domain.post.service.PostService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
public class ApiV1PostController {

    // PostService 주입
    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public GenericResponse<PostResponseDto> createPost
    (@Valid @RequestBody PostRequestDto responseDto,
            @AuthenticationPrincipal CustomUserDetails user) {

        PostResponseDto createdPost = postService.createPost(responseDto, user.getSiteUser());
        return GenericResponse.of(true, HttpStatus.CREATED.value(), createdPost);
    }

    //  전체 게시글 조회
    //  1. 조건 없이 전체 글 조회 2. 카테고리 3. 정렬 4.검색 5.페이징
    @GetMapping
    public GenericResponse<Page<PostResponseDto>> getAllPosts(
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sort", defaultValue = "latest") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentUserId = (userDetails != null) ? userDetails.getSiteUser().getId() : null;

        Page<PostResponseDto> posts = postService.getAllPosts(categoryId, keyword, sort,
                page, size, currentUserId);

        return GenericResponse.of(true, HttpStatus.OK.value(), posts);
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public GenericResponse<PostResponseDto> getPostById(@PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            throw new GlobalException(GlobalErrorCode.UNAUTHENTICATION_USER);
        }
        Long currentUserId =
                (customUserDetails != null) ? customUserDetails.getSiteUser().getId() : null;
        PostResponseDto post = postService.getPostById(id, currentUserId);
        return GenericResponse.of(true, HttpStatus.OK.value(), post);
    }

    // 게사글 수정
    @PutMapping("/{id}")
    public GenericResponse<PostResponseDto> updatePost(@PathVariable Long id,
            @Valid @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponseDto updatedPost = postService.updatePost
                (id, requestDto, userDetails.getSiteUser().getId());
        return GenericResponse.of(true, HttpStatus.OK.value(), updatedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public GenericResponse<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal
    CustomUserDetails user) {
        postService.deletePost(id, user.getSiteUser().getId());
        return GenericResponse.of(true, HttpStatus.OK.value());
    }
}
