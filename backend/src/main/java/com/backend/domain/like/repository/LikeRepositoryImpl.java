package com.backend.domain.like.repository;

import com.backend.domain.like.entity.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

	private final LikeJpaRepository likeJpaRepository;

	@Override
	public Like save(Like like) {
		return likeJpaRepository.save(like);
	}
}
