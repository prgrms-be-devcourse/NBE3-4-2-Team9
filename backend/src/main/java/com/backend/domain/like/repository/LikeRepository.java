package com.backend.domain.like.repository;

import com.backend.domain.like.domain.LikeType;
import com.backend.domain.like.entity.Like;

/**
 * LikeRepository
 * <p>Like 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
public interface LikeRepository {

	/**
	 * @param like Like 객체
	 * @return {@link Like}
	 * @implSpec Like 저장 메서드 입니다.
	 */
	Like save(Like like);

	/**
	 * @param jobPostingId jobPostingId
	 * @param likeType likeType {@link LikeType}
	 * @return {@link Boolean} 데이터 존재할 시 true, 존재하지 않을 때 false
	 * @implSpec Like exists 메서드 입니다.
	 */
	boolean existsByJobPostingId(Long jobPostingId, LikeType likeType);
}
