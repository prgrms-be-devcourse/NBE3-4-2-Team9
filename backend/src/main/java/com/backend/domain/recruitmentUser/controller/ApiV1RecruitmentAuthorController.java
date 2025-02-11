package com.backend.domain.recruitmentUser.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.recruitmentUser.dto.request.AuthorRequest;
import com.backend.domain.recruitmentUser.dto.response.RecruitmentUserPageResponse;
import com.backend.domain.recruitmentUser.service.RecruitmentAuthorService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitment")
public class ApiV1RecruitmentAuthorController {

    private final RecruitmentAuthorService recruitmentAuthorService;

    /**
     * 모집 지원 승인 작성자가 특정 지원자의 모집 신청을 승인합니다.
     *
     * @param userDetails 현재 로그인한 작성자
     * @param postId      모집 게시글 ID
     * @param request     모집 승인 요청 (userId 포함)
     * @return 성공 응답 (200 OK)
     */
    @PatchMapping("/{postId}/accept")
    public GenericResponse<Void> approveRecruitment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody @Valid AuthorRequest request) {

        recruitmentAuthorService.recruitmentAccept(userDetails.getSiteUser(), postId,
                request.userId());
        return GenericResponse.of(true, HttpStatus.OK.value());
    }

    /**
     * 모집 지원 거절 작성자가 특정 지원자의 모집 신청을 거절합니다.
     *
     * @param userDetails 현재 로그인한 작성자
     * @param postId      모집 게시글 ID
     * @param request     모집 거절 요청 (userId 포함)
     * @return 성공 응답 (200 OK)
     */
    @PatchMapping("/{postId}/reject")
    public GenericResponse<Void> rejectRecruitment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody @Valid AuthorRequest request) {

        recruitmentAuthorService.recruitmentReject(
                userDetails.getSiteUser(),
                postId,
                request.userId()
        );
        return GenericResponse.of(true, HttpStatus.OK.value());
    }

    /**
     * 모집 지원자 목록 조회 작성자가 본인의 모집 게시글에 지원한 사용자 목록을 조회합니다.
     *
     * @param userDetails 현재 로그인한 작성자
     * @param postId      모집 게시글 ID
     * @param pageNum     페이지 번호 (기본값: 0)
     * @param pageSize    페이지 크기 (기본값: 10)
     * @return 지원자 목록 (DTO 변환)
     */
    @GetMapping("/{postId}/applied-users")
    public GenericResponse<RecruitmentUserPageResponse> getAppliedUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt"));

        RecruitmentUserPageResponse appliedUsers = recruitmentAuthorService.getAppliedUserList(
                userDetails.getSiteUser(),
                postId,
                pageable
        );
        return GenericResponse.of(true, HttpStatus.OK.value(), appliedUsers);
    }

    /**
     * 모집 승인된 참여자 목록 조회 모집이 완료된 후 승인된 지원자 목록을 조회합니다.
     *
     * @param userDetails 현재 로그인한 작성자
     * @param postId      모집 게시글 ID
     * @param pageNum     페이지 번호 (기본값: 0)
     * @param pageSize    페이지 크기 (기본값: 10)
     * @return 모집된 참여자 목록 (DTO 변환)
     */
    @GetMapping("/{postId}/accepted-users")
    public GenericResponse<RecruitmentUserPageResponse> getAcceptedUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt"));

        RecruitmentUserPageResponse acceptedUsers = recruitmentAuthorService.getAcceptedUserList(
                userDetails.getSiteUser(),
                postId,
                pageable
        );
        return GenericResponse.of(true, HttpStatus.OK.value(), acceptedUsers);
    }
}
