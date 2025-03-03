package com.backend.domain.recruitmentUser.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.post.entity.RecruitmentPost;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.recruitment.RecruitmentPostRepository;
import com.backend.domain.recruitmentUser.dto.response.RecruitmentUserPageResponse;
import com.backend.domain.recruitmentUser.entity.RecruitmentUser;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.mail.service.MailService;
import com.backend.global.mail.util.TemplateName;

import lombok.RequiredArgsConstructor;

/**
 * 모집 관리 서비스 (작성자가 모집 지원자를 관리)
 * <p>
 * 기능: - 모집 지원 승인 (recruitmentAccept) - 모집 지원 거절 (recruitmentReject) - 모집 지원자 목록 조회
 * (getAppliedUserList) - 모집 승인된 참여자 목록 조회 (getAcceptedUserList)
 */
@Service
@RequiredArgsConstructor
public class RecruitmentAuthorService {

	private final MailService mailService;
	private final RecruitmentUserRepository recruitmentUserRepository;
	private final RecruitmentPostRepository recruitmentPostRepository;

	// ==============================
	//  1. 모집 지원 처리 (승인 / 거절)
	// ==============================

	/**
	 * 모집 지원 승인 작성자가 특정 지원자의 모집 신청을 승인합니다.
	 *
	 * @param author 현재 로그인한 작성자
	 * @param postId 모집 게시글 ID
	 * @param userId 모집 신청자 ID
	 * @throws GlobalException 모집 신청 내역이 없거나, 작성자가 아닐 경우 예외 발생
	 */
	@Transactional
	public void recruitmentAccept(SiteUser author, Long postId, Long userId) {
		RecruitmentPost post = validateAuthorAndGetPost(author, postId);
		RecruitmentUser recruitmentUser = getRecruitmentUser(userId, postId);

		validateRecruitmentNotClosed(post);
		validateRecruitmentUserStatus(recruitmentUser);

		// 인원 증가, 모집 승인
		recruitmentUser.accept();

		// 모집 완료시 종료 상태로 수정
		updateRecruitmentStatus(post);
	}

	/**
	 * 모집 지원 거절 작성자가 특정 지원자의 모집 신청을 거절합니다.
	 *
	 * @param author 현재 로그인한 작성자
	 * @param postId 모집 게시글 ID
	 * @param userId 모집 신청자 ID
	 * @throws GlobalException 모집 신청 내역이 없거나, 작성자가 아닐 경우 예외 발생
	 */
	@Transactional
	public void recruitmentReject(SiteUser author, Long postId, Long userId) {
		RecruitmentPost post = validateAuthorAndGetPost(author, postId);
		RecruitmentUser recruitmentUser = getRecruitmentUser(userId, postId);

		validateRecruitmentNotClosed(post);
		validateRecruitmentUserStatus(recruitmentUser);

		recruitmentUser.reject();
	}

	// ==============================
	//  2. 모집 지원자 조회
	// ==============================

	/**
	 * 모집 지원자 목록 조회 작성자가 본인의 모집 게시글에 지원한 사용자 목록을 조회합니다.
	 *
	 * @param author 게시글 작성자
	 * @param postId 모집 게시글 ID
	 * @return 지원자 목록 (DTO 변환)
	 * @throws GlobalException 작성자가 아닐 경우 예외 발생
	 */
	@Transactional(readOnly = true)
	public RecruitmentUserPageResponse getAppliedUserList(SiteUser author, Long postId,
		Pageable pageable) {
		RecruitmentPost post = validateAuthorAndGetPost(author, postId);

		Page<RecruitmentUser> appliedUsers = recruitmentUserRepository.findAllByPost_PostIdAndStatus(
			post.getPostId(), RecruitmentUserStatus.APPLIED, pageable);

		return RecruitmentUserPageResponse.from(postId, appliedUsers);
	}

	/**
	 * 모집 승인된 참여자 목록 조회 모집이 완료된 후 승인된 지원자 목록을 조회합니다.
	 *
	 * @param author 게시글 작성자
	 * @param postId 모집 게시글 ID
	 * @return 모집된 참여자 목록 (DTO 변환)
	 * @throws GlobalException 작성자가 아닐 경우 예외 발생
	 */
	@Transactional(readOnly = true)
	public RecruitmentUserPageResponse getAcceptedUserList(SiteUser author, Long postId,
		Pageable pageable) {
		RecruitmentPost post = validateAuthorAndGetPost(author, postId);

		Page<RecruitmentUser> acceptedUsers = recruitmentUserRepository.findAllByPost_PostIdAndStatus(
			post.getPostId(), RecruitmentUserStatus.ACCEPTED, pageable);

		return RecruitmentUserPageResponse.from(postId, acceptedUsers);
	}



	// ==============================
	//  3. 검증 메서드
	// ==============================

	/**
	 * 모집 신청 내역 조회 userId와 postId를 기준으로 모집 신청 내역을 조회합니다.
	 *
	 * @param userId 모집 신청자 ID
	 * @param postId 모집 게시글 ID
	 * @return 모집 신청 내역 엔티티
	 * @throws GlobalException 모집 신청 내역이 존재하지 않을 경우 예외 발생
	 */

	private RecruitmentUser getRecruitmentUser(Long userId, Long postId) {
		return recruitmentUserRepository.findByPost_PostIdAndSiteUser_Id(postId, userId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.RECRUITMENT_NOT_FOUND));
	}

	/**
	 * 모집이 종료된 경우 예외 발생 모집이 이미 마감된 경우 예외를 발생시킵니다.
	 *
	 * @param post 모집 게시글
	 * @throws GlobalException 모집이 이미 종료된 경우 예외 발생
	 */
	private void validateRecruitmentNotClosed(RecruitmentPost post) {
		if (post.getRecruitmentStatus() == RecruitmentStatus.CLOSED) {
			throw new GlobalException(GlobalErrorCode.RECRUITMENT_CLOSED);
		}
	}

	/**
	 * 모집 신청자의 상태 검증 지원자의 상태가 APPLIED가 아닐 경우 예외를 발생시킵니다.
	 *
	 * @param recruitmentUser 모집 신청자 엔티티
	 * @throws GlobalException 지원자의 상태가 APPLIED가 아닐 경우 예외 발생
	 */
	private static void validateRecruitmentUserStatus(RecruitmentUser recruitmentUser) {
		if (recruitmentUser.getStatus() != RecruitmentUserStatus.APPLIED) {
			throw new GlobalException(GlobalErrorCode.INVALID_RECRUITMENT_STATUS);
		}
	}

	/**
	 * 모집 게시글 검증 및 조회 작성자인지 확인 후 게시글을 반환합니다.
	 *
	 * @param author 게시글 작성자
	 * @param postId 모집 게시글 ID
	 * @return 게시글 엔티티 (작성자 검증 완료)
	 * @throws GlobalException 게시글이 존재하지 않거나 작성자가 아닐 경우 예외 발생
	 */
	private RecruitmentPost validateAuthorAndGetPost(SiteUser author, Long postId) {
		RecruitmentPost post = recruitmentPostRepository.findByIdFetch(postId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

		if (!post.getAuthor().getId().equals(author.getId())) {
			throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
		}

		return post;
	}

	/**
	 * 모집 상태 업데이트 현재 모집된 인원과 모집 가능 인원을 비교하여 모집 마감 여부를 결정합니다.
	 *
	 * @param post 모집 게시글
	 */
	@Transactional
	public void updateRecruitmentStatus(RecruitmentPost post) {
		// Status가 null인 경우 OPEN으로 기본 값 설정
		if (post.getRecruitmentStatus() == null) {
			post.updateRecruitmentStatus(RecruitmentStatus.OPEN);
		}

		// 현재 인원이 모집 인원과 같은지 테스트
		if (post.getNumOfApplicants() <= recruitmentUserRepository.countAcceptedByPostId(post.getPostId())) {
			post.updateRecruitmentStatus(RecruitmentStatus.CLOSED);
		}

		recruitmentPostRepository.save(post);

		// 모집 상태 Closed 된 게시글에서 ACCEPTED 상태의 유저 이메일 리스트 반환
		List<String> emailList = recruitmentUserRepository.findAcceptedByClosed(post.getPostId())
			.stream()
			.map(ru -> ru.getSiteUser().getEmail())
			.collect(Collectors.toList());

		mailService.sendDeliveryStartEmail(emailList, TemplateName.RECRUITMENT_CHAT, post.getPostId());
	}
}
