package com.backend.domain.like.controller;

import com.backend.domain.like.dto.LikeCreateRequest;
import com.backend.domain.like.dto.LikeCreateResponse;
import com.backend.domain.like.service.LikeService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LikeController
 * <p>관심 컨트롤러 입니다.</p>
 *
 * @author Kim Dong O
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class ApiV1LikeController {

	private final LikeService likeService;

	@PostMapping
	public GenericResponse<LikeCreateResponse> create(
		@RequestBody @Validated LikeCreateRequest likeCreateRequest, @AuthenticationPrincipal
	CustomUserDetails customUserDetails) {

		LikeCreateResponse likeCreateResponse = likeService.save(customUserDetails.getSiteUser(),
			likeCreateRequest.targetId(),
			likeCreateRequest.likeType());

		return GenericResponse.of(true, HttpStatus.CREATED.value(), likeCreateResponse);
	}
}
