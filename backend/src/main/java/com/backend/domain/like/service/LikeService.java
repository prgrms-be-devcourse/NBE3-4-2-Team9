package com.backend.domain.like.service;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.like.domain.LikeType;
import com.backend.domain.like.dto.LikeCreateResponse;
import com.backend.domain.like.entity.Like;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * LikeService
 * <p>관심 서비스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final JobPostingRepository jobPostingRepository;

	/**
	 * 관심 저장 메서드 입니다.
	 *
	 * @param siteUser 로그인 유저
	 * @param targetId 관심 타겟 ID
	 * @param likeType {@link LikeType} 관심 타입
	 * @return {@link LikeCreateResponse}
	 * @throws GlobalException 이미 관심 저장이 되어 있을 때 또는 지원하지 않는 LikeType 일 때 발생
	 */
	public LikeCreateResponse save(SiteUser siteUser, Long targetId, LikeType likeType) {
		existsCheck(siteUser.getId(), targetId, likeType);
		Long resultId = null;

		switch (likeType) {
			case JOB_POSTING -> {

				JobPosting jobPosting = JobPosting.builder()
					.id(targetId)
					.build();

				Like saveLike = Like.builder()
					.siteUser(siteUser)
					.jobPosting(jobPosting)
					.likeType(likeType)
					.build();

				resultId = likeRepository.save(saveLike).getId();
			}
			case POST -> {
				//TODO Post 추가시 로직 구현 예정
			}
			default -> throw new GlobalException(GlobalErrorCode.NOT_SUPPORT_TYPE);
		}

		return LikeCreateResponse.builder()
			.targetId(resultId)
			.likeType(likeType)
			.build();
	}

	/**
	 * 특정 targetId에 좋아요를 눌렀는지 체크합니다.
	 * <p>
	 * 주어진 targetId에 좋아요를 눌렀는지 체크합니다.
	 * <br> 존재하지 않을 경우 예외를 발생시킵니다.
	 * </p>
	 *
	 * @param siteUserId siteUserId
	 * @param targetId   targetId
	 * @param likeType   검사할 타입
	 * @throws GlobalException 데이터가 존재하지 않을 경우 발생
	 */
	private void existsCheck(Long siteUserId, Long targetId, LikeType likeType) {
		boolean result = false;

		switch (likeType) {
			case JOB_POSTING ->
				result = likeRepository.existsByJobPostingId(siteUserId, targetId, likeType);
			case POST -> result = false; //TODO 추후 변경 예정
		}

		if (!result) {
			throw new GlobalException(GlobalErrorCode.ALREADY_LIKE);
		}
	}
}
