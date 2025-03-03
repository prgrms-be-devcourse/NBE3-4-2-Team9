package com.backend.domain.post.repository.recruitment;

import com.backend.domain.post.dto.RecruitmentPostResponse;
import com.backend.domain.post.entity.RecruitmentPost;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecruitmentPostRepositoryImpl implements RecruitmentPostRepository {

	private final RecruitmentPostJpaRepository recruitmentPostJpaRepository;
	private final RecruitmentPostQueryRepository recruitmentPostQueryRepository;

	@Override
	public Optional<RecruitmentPost> findById(Long id) {
		return recruitmentPostJpaRepository.findById(id);
	}

	@Override
	public Optional<RecruitmentPost> findByIdFetch(Long id) {
		return recruitmentPostJpaRepository.findByIdFetch(id);
	}

	@Override
	public RecruitmentPost save(RecruitmentPost recruitmentPost) {
		return recruitmentPostJpaRepository.save(recruitmentPost);
	}

	@Override
	public void deleteById(Long id) {
		recruitmentPostJpaRepository.deleteById(id);
	}

	@Override
	public List<RecruitmentPost> findAll() {
		return recruitmentPostJpaRepository.findAll();
	}

	@Override
	public Optional<RecruitmentPostResponse> findPostResponseById(Long postId, Long siteUserId) {
		return recruitmentPostQueryRepository.findPostResponseById(postId, siteUserId);
	}
}
