package com.backend.domain.like.repository;

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
}
