package com.backend.domain.post.controller;

import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.service.FreePostService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/free/posts")
public class ApiV1FreePostController {

	private final FreePostService freePostService;

	@PostMapping
	public GenericResponse<PostCreateResponse> save(
		@Valid FreePostRequest freePostRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		PostCreateResponse postCreateResponse = freePostService.save(freePostRequest,
			customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.CREATED.value(), postCreateResponse);
	}

	@DeleteMapping("/{postId}")
	public GenericResponse<Void> delete(
		@PathVariable("postId") Long postId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		freePostService.delete(postId, customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.OK.value());
	}
}
