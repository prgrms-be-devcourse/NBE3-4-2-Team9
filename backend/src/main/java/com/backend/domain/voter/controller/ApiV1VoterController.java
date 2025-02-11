package com.backend.domain.voter.controller;

import com.backend.domain.voter.dto.VoterCreateRequest;
import com.backend.domain.voter.dto.VoterCreateResponse;
import com.backend.domain.voter.service.VoterService;
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
 * ApiV1VoterController
 * <p>추천 컨트롤러 입니다.</p>
 *
 * @author Kim Dong O
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voter")
public class ApiV1VoterController {

	private final VoterService voterService;

	@PostMapping
	public GenericResponse<VoterCreateResponse> create(
		@RequestBody @Validated VoterCreateRequest voterCreateRequest, @AuthenticationPrincipal
	CustomUserDetails customUserDetails) {

		VoterCreateResponse voterCreateResponse = voterService.save(customUserDetails.getSiteUser(),
			voterCreateRequest.targetId(),
			voterCreateRequest.voterType());

		return GenericResponse.of(true, HttpStatus.CREATED.value(), voterCreateResponse);
	}
}
