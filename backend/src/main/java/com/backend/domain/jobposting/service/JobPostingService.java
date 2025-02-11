package com.backend.domain.jobposting.service;

import com.backend.domain.jobposting.dto.JobPostingDetailResponse;
import com.backend.domain.jobposting.dto.JobPostingPageResponse;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.jobposting.util.JobPostingSearchCondition;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.domain.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JobPostingService
 * <p>채용 공고 서비스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Service
@RequiredArgsConstructor
public class JobPostingService {

	private final JobPostingRepository jobPostingRepository;

	/**
	 * 채용 공고 동적 페이징 조회 메서드 입니다.
	 *
	 * @param jobPostingSearchCondition 조회 조건 객체 {@link JobPostingSearchCondition}
	 * @return {@link Page<JobPostingPageResponse>}
	 */
	@Transactional(readOnly = true)
	public Page<JobPostingPageResponse> findAll(
		JobPostingSearchCondition jobPostingSearchCondition) {
		int pageNum = jobPostingSearchCondition.pageNum() == null ?
			0 : jobPostingSearchCondition.pageNum();

		int pageSize = jobPostingSearchCondition.pageSize() == null ?
			10 : jobPostingSearchCondition.pageSize();

		Pageable pageable = PageRequest.of(pageNum, pageSize);

		return jobPostingRepository.findAll(jobPostingSearchCondition, pageable);
	}

	/**
	 * 채용 공고 상세 조회 메서드 입니다.
	 *
	 * @param jobPostingId 채용 공고 ID
	 * @param siteUserId   회원 ID
	 * @return {@link JobPostingDetailResponse}
	 */
	@Transactional(readOnly = true)
	public JobPostingDetailResponse findDetailById(Long jobPostingId, Long siteUserId) {

		return jobPostingRepository.findDetailById(jobPostingId, siteUserId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.JOB_POSTING_NOT_FOUND));
	}

	public Page<JobPostingPageResponse> findAllVoter(
		JobPostingSearchCondition jobPostingSearchCondition, SiteUser siteUser) {

		Pageable pageable = PageRequest.of(
			jobPostingSearchCondition.pageNum(), jobPostingSearchCondition.pageSize()
		);

		return jobPostingRepository.findAllVoter(jobPostingSearchCondition, siteUser.getId(), pageable);
	}
}
