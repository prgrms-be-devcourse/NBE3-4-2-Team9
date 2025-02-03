package com.backend.domain.board.controller;

import com.backend.domain.board.dto.BoardCreateRequest;
import com.backend.domain.board.dto.BoardResponse;
import com.backend.domain.board.service.PostService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class ApiV1BoardController {

	// PostService 주입
	private final PostService postService;

	// 게시글 생성 (DTO 적용)
	// TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @PostMapping("/posts")
//    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostResponseDto responseDto){
//        PostResponseDto createdPost = postService.creatPost(responseDto);
//        return ResponseEntity.ok(createdPost);
//    }

	// 전체 게시글 조회 (DTO 적용) + 카테고리, 정렬, 검색, 페이징 기능 추가
//    @GetMapping("/posts")
//    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
//            @RequestParam(required = false) Long category,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(defaultValue = "latest") String sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size){
//
//        Page<PostResponseDto> posts = postService.getAllPosts(category, keyword, sort, page, size);
//        return ResponseEntity.ok(posts);
//    }

	// 특정 게시글 조회 (DTO 적용)
	@GetMapping("/posts/{id}")
	public GenericResponse<BoardResponse> getPostById(@PathVariable Long id) {
		BoardResponse post = postService.getPostById(id);
		return GenericResponse.of(true, HttpStatus.OK.value(), post);
	}

	// 게사글 수정 (DTO 적용)
	@PutMapping("/posts/{id}")
	public ResponseEntity<BoardResponse> updatePost(@PathVariable Long id,
		@RequestBody BoardCreateRequest requestDto) {
		BoardResponse updatedPost = postService.updatePost(id, requestDto);
		return ResponseEntity.ok(updatedPost);
	}

	// 게시글 삭제
	@DeleteMapping("/posts/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Long id) {
		postService.deletePost(id);
		return ResponseEntity.noContent().build();
	}
}
