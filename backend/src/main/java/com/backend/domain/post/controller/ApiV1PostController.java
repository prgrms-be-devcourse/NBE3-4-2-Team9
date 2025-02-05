package com.backend.domain.post.controller;

import com.backend.domain.post.dto.PostCreateRequestDto;
import com.backend.domain.post.dto.PostResponseDto;
import com.backend.domain.post.service.PostService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    (@Valid @RequestBody PostCreateRequestDto responseDto,
            @AuthenticationPrincipal CustomUserDetails user) {

        PostResponseDto createdPost = postService.createPost(responseDto, user.getSiteUser());
        return GenericResponse.of(true, HttpStatus.CREATED.value(), createdPost);
    }

    //  전체 게시글 조회 (DTO 적용) + 조건 없이 전체 글 조회, 카테고리, 정렬, 검색, 페이징
    @GetMapping
    public GenericResponse<Page<PostResponseDto>> getAllPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponseDto> posts = postService.getAllPosts(categoryId, keyword, sort,
                page, size);

        return GenericResponse.of(true, HttpStatus.OK.value(), posts);
    }

    // 특정 게시글 조회 (DTO 적용)
    @GetMapping("/{id}")
    public GenericResponse<PostResponseDto> getPostById(@PathVariable Long id) {

        PostResponseDto post = postService.getPostById(id);

        return GenericResponse.of(true, HttpStatus.OK.value(), post);
    }

    // 게사글 수정 (DTO 적용)
//    @PutMapping("/{id}")
//    public GenericResponse<PostResponseDto> updatePost(@PathVariable Long id,
//    @RequestBody PostCreateRequestDto requestDto){
//        PostResponseDto updatedPost = postService.updatePost(id, requestDto);
//        return GenericResponse.of();
//    }

    // 게시글 삭제
//    @DeleteMapping("/{id}")
//    public GenericResponse<Void> deletePost(@PathVariable Long id){
//        postService.deletePost(id);
//        return GenericResponse.noContent().build();
//    }
}
