package com.backend.domain.recruitmentUser.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.recruitmentUser.dto.response.RecruitmentPostResponse;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.backend.domain.recruitmentUser.service.RecruitmentUserService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * 모집 신청 및 조회를 담당하는 컨트롤러 요청 경로: /api/v1/recruitment-user
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitment")
public class ApiV1RecruitmentUserController {

    private final RecruitmentUserService recruitmentUserService;

    /**
     * 모집 신청 사용자가 특정 게시글에 모집을 신청합니다.
     *
     * @param userDetails 현재 로그인한 사용자
     * @param postId      모집할 게시글 ID (URL Path)
     * @return 성공 응답 (201 Created)
     */
    @PostMapping("/{postId}")
    public GenericResponse<Void> applyRecruitment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        recruitmentUserService.saveRecruitment(
                userDetails.getSiteUser(),
                postId
        );

        return GenericResponse.of(true, HttpStatus.CREATED.value());
    }

    /**
     * 모집 신청 취소 사용자가 본인의 모집 신청을 취소합니다.
     *
     * @param userDetails 현재 로그인된 사용자
     * @param postId      모집 취소할 게시글 ID (URL Path)
     * @return 성공 응답 (200 OK)
     */
    @DeleteMapping("/{postId}")
    public GenericResponse<Void> cancelRecruitment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        recruitmentUserService.cancelRecruitment(
                userDetails.getSiteUser(),
                postId
        );

        return GenericResponse.of(true, HttpStatus.OK.value());
    }

    /**
     * 모집 승인된 게시글 조회 사용자가 특정 모집 상태(기본값: ACCEPTED)인 게시글 목록을 페이징하여 조회합니다.
     *
     * @param userDetails 현재 로그인된 사용자
     * @param status      모집 상태 (기본값: "ACCEPTED")
     * @param pageNum     페이지 번호 (기본값: 0)
     * @param pageSize    페이지 크기 (기본값: 10)
     * @return 모집 승인된 게시글 목록 (Page<PostResponseDto>)
     */
    @GetMapping("/posts")
    public GenericResponse<RecruitmentPostResponse> getAcceptedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "ACCEPTED") RecruitmentUserStatus status,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt"));

        RecruitmentPostResponse acceptedPosts = recruitmentUserService.getAcceptedPosts(
                userDetails.getSiteUser(),
                status,
                pageable
        );

        return GenericResponse.of(true, HttpStatus.OK.value(), acceptedPosts);
    }
}
