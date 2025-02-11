package com.backend.domain.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.RecruitmentPostRequest;
import com.backend.domain.post.service.RecruitmentPostService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitment/posts")
public class ApiV1RecruitmentPostController {

	private final RecruitmentPostService recruitmentPostService;

	// 모집 게시글 생성
	@PostMapping
	public GenericResponse<PostCreateResponse> createPost(
		@RequestBody @Valid RecruitmentPostRequest recruitmentPostRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		PostCreateResponse postCreateResponse = recruitmentPostService.save(
			recruitmentPostRequest,
			customUserDetails.getSiteUser()
		);
		return GenericResponse.of(true, HttpStatus.CREATED.value(), postCreateResponse);
	}

	// 모집 게사글 수정
	@PutMapping("/{postId}")
	public GenericResponse<PostResponse> updatePost(
		@PathVariable(name = "postId") Long postId,
		@RequestBody @Valid RecruitmentPostRequest requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		PostResponse result = recruitmentPostService.update(postId, requestDto, userDetails.getSiteUser());
		return GenericResponse.of(true, HttpStatus.OK.value(), result);
	}

	// 모집 게시글 삭제
	@DeleteMapping("/{postId}")
	public GenericResponse<Void> deletePost(
		@PathVariable(name = "postId") Long postId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		recruitmentPostService.delete(postId, customUserDetails.getSiteUser());
		return GenericResponse.of(true, HttpStatus.OK.value());
	}

}
