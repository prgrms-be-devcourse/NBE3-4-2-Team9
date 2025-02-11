package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.dto.JobPostingDetailResponse;
import com.backend.domain.jobposting.dto.JobPostingPageResponse;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.util.JobPostingSearchCondition;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepository {

	private final JobPostingJpaRepository jobPostingJpaRepository;
	private final JobPostingQueryRepository jobPostingQueryRepository;

	@Override
	public Optional<JobPosting> findById(Long id) {
		return jobPostingJpaRepository.findById(id);
	}

	@Override
	public Optional<JobPostingDetailResponse> findDetailById(Long jobPostingId, Long siteUserId) {
		return jobPostingQueryRepository.findDetailById(jobPostingId, siteUserId);
	}

	@Override
	public JobPosting save(JobPosting jobPosting) {
		return jobPostingJpaRepository.save(jobPosting);
	}

	@Override
	public List<JobPosting> findAll() {
		return jobPostingJpaRepository.findAll();
	}

	@Override
	public Page<JobPostingPageResponse> findAll(JobPostingSearchCondition jobPostingSearchCondition, Pageable pageable) {
		return jobPostingQueryRepository.findAll(jobPostingSearchCondition, pageable);
	}

	@Override
	public boolean existsById(Long jobPostingId) {
		return jobPostingJpaRepository.existsById(jobPostingId);
	}

	@Override
	public List<JobPosting> saveAll(List<JobPosting> publicDataList) {
		return jobPostingJpaRepository.saveAll(publicDataList);
	}

	@Override
	public List<Long> findIdsAll() {
		return jobPostingJpaRepository.findIdsAll();
	}

	@Override
	public Page<JobPostingPageResponse> findAllVoter(
		JobPostingSearchCondition jobPostingSearchCondition, Long siteUserId, Pageable pageable) {
		return jobPostingQueryRepository.findAllVoter(jobPostingSearchCondition, siteUserId, pageable);
	}
}
