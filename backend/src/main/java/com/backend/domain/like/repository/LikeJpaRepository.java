package com.backend.domain.like.repository;

import com.backend.domain.like.domain.LikeType;
import com.backend.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
	boolean existsByJobPostingIdAndLikeType(Long jobPostingId, LikeType likeType);
}