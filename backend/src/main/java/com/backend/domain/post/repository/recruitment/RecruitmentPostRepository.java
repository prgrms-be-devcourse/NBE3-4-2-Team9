package com.backend.domain.post.repository.recruitment;

import com.backend.domain.post.dto.RecruitmentPostResponse;
import com.backend.domain.post.entity.RecruitmentPost;
import java.util.List;
import java.util.Optional;

public interface RecruitmentPostRepository {
	Optional<RecruitmentPost> findById(Long id);

	Optional<RecruitmentPost> findByIdFetch(Long id);

	RecruitmentPost save(RecruitmentPost recruitmentPost);

	void deleteById(Long id);

	List<RecruitmentPost> findAll();

	Optional<RecruitmentPostResponse> findPostResponseById(Long postId, Long siteUserId);
}
