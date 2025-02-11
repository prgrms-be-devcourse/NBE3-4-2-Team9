package com.backend.domain.recruitmentUser.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.recruitmentUser.dto.response.RecruitmentPostResponse;
import com.backend.domain.recruitmentUser.entity.RecruitmentUser;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

/**
 * RecruitmentUserService 유저 모집 신청 및 모집 관련 조회를 담당하는 서비스 클래스입니다.
 *
 * @author Hyeonsuk
 */
@Service
@RequiredArgsConstructor
public class RecruitmentUserService {

    private final RecruitmentUserRepository recruitmentUserRepository;
    private final PostRepository postRepository;

    // ==============================
    //  1. 비즈니스 로직
    // ==============================

    /**
     * 모집 신청 등록 특정 모집 게시글에 대해 사용자가 신청을 진행합니다.
     *
     * @param siteUser 모집 신청을 하는 사용자
     * @param postId   모집 게시글 ID
     * @throws GlobalException 모집 게시글이 존재하지 않거나, 모집이 종료된 경우 예외 발생
     * @throws GlobalException 사용자가 이미 해당 게시글에 모집 신청을 한 경우 예외 발생
     */
    @Transactional
    public void saveRecruitment(SiteUser siteUser, Long postId) {
        Post post = getPost(postId);

        // 모집 신청 가능 여부 검증
        checkRecruitmentCondition(siteUser, post);

        // 모집 신청 정보 저장
        RecruitmentUser recruitmentUser = RecruitmentUser.builder()
                .post(post)
                .siteUser(siteUser)
                .status(RecruitmentUserStatus.APPLIED) // 기본 상태: APPLIED
                .build();

        recruitmentUserRepository.save(recruitmentUser);
    }

    /**
     * 모집 신청 취소 사용자가 본인의 모집 신청을 취소합니다.
     *
     * @param siteUser 모집 신청을 취소하는 사용자
     * @param postId   모집 게시글 ID
     * @throws GlobalException 모집 게시글이 존재하지 않거나, 모집이 종료된 경우 예외 발생
     * @throws GlobalException 사용자가 해당 게시글에 모집 신청을 하지 않은 경우 예외 발생
     */
    @Transactional
    public void cancelRecruitment(SiteUser siteUser, Long postId) {
        Post post = getPost(postId);

        // 모집이 종료된 경우 취소 불가
        validateRecruitmentNotClosed(post);

        // 사용자의 모집 신청 내역 조회
        RecruitmentUser recruitmentUser = getRecruitmentUser(siteUser, postId);

        // 모집 신청 삭제
        recruitmentUserRepository.delete(recruitmentUser);
    }

    /**
     * 사용자가 특정 상태(ACCEPTED 등)인 모집 게시글 조회 사용자가 특정 상태(ACCEPTED, APPLIED, REJECTED)
     * 기본값(ACCEPTED) 인 모집 게시글을 페이징하여 조회합니다.
     *
     * @param siteUser 현재 로그인한 사용자
     * @param status   조회할 모집 상태 (ACCEPTED, APPLIED, REJECTED 등)
     * @param pageable 페이징 정보
     * @return 사용자가 특정 상태로 참여한 모집 게시글 목록 (Page<PostResponseDto>)
     */
    public RecruitmentPostResponse getAcceptedPosts(
            SiteUser siteUser,
            RecruitmentUserStatus status,
            Pageable pageable) {

            // RecruitmentUser 목록을 페이징하여 조회 (특정 사용자와 상태에 맞는 모집 지원자들)
            Page<RecruitmentUser> recruitmentUsers = recruitmentUserRepository
                .findAllBySiteUser_IdAndStatus(siteUser.getId(), status, pageable);

            // RecruitmentUser에서 Post만 추출하여 Page<Post>로 변환
            Page<Post> posts = recruitmentUsers.map(RecruitmentUser::getPost);

            return RecruitmentPostResponse.from(status, posts);
    }

    // ==============================
    //  2. 유효성 검증 메서드
    // ==============================

    /**
     * 이미 해당 게시글에 모집 신청했는지 확인
     *
     * @param siteUser 모집 신청을 진행하는 사용자
     * @param postId   모집 게시글 ID
     * @return true - 이미 지원함 / false - 지원하지 않음
     */
    private boolean isAlreadyApplied(SiteUser siteUser, Long postId) {
        return recruitmentUserRepository.findByPost_PostIdAndSiteUser_Id(postId, siteUser.getId())
                .isPresent();
    }

    /**
     * 모집이 종료된 경우 예외 발생
     *
     * @param post 모집 게시글
     * @throws GlobalException 모집이 이미 종료된 경우 예외 발생
     */
    private void validateRecruitmentNotClosed(Post post) {
        if (post.getRecruitmentStatus() == RecruitmentStatus.CLOSED) {
            throw new GlobalException(GlobalErrorCode.RECRUITMENT_CLOSED);
        }
    }

    /**
     * 모집 신청 가능 여부 검증 다음과 같은 경우 모집 신청이 불가능합니다. - 이미 해당 게시글에 모집 신청을 한 경우 - 모집이 종료된 경우
     *
     * @param siteUser 모집 신청을 진행하는 사용자
     * @param post     모집 게시글
     * @throws GlobalException 위 조건 중 하나라도 만족할 경우 예외 발생
     */
    private void checkRecruitmentCondition(SiteUser siteUser, Post post) {
        // 중복 지원 여부 확인
        if (isAlreadyApplied(siteUser, post.getPostId())) {
            throw new GlobalException(GlobalErrorCode.ALREADY_RECRUITMENT);
        }

        // 모집 종료 여부 검증
        validateRecruitmentNotClosed(post);
    }

    // ==============================
    //  3. DB 조회 메서드
    // ==============================

    /**
     * 모집 신청 내역 조회 특정 사용자의 모집 신청 내역을 조회합니다.
     *
     * @param siteUser 모집 신청자
     * @param postId   모집 게시글 ID
     * @return 모집 신청 내역 엔티티
     * @throws GlobalException 모집 신청 내역이 존재하지 않을 경우 예외 발생
     */
    private RecruitmentUser getRecruitmentUser(SiteUser siteUser, Long postId) {
        return recruitmentUserRepository.findByPost_PostIdAndSiteUser_Id(postId, siteUser.getId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.RECRUITMENT_NOT_FOUND));
    }

    /**
     * 모집 게시글 조회 주어진 모집 게시글 ID에 해당하는 게시글을 조회하며, 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @param postId 모집 게시글 ID
     * @return 조회된 모집 게시글 (Post 엔티티)
     * @throws GlobalException 게시글이 존재하지 않을 경우 예외 발생
     */
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
    }
}
