package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.dto.JobPostingPageResponse;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.util.JobPostingSearchCondition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * JobPostingRepository
 * <p>JobPosting 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
public interface JobPostingRepository {

	/**
	 * @param id JobPosting id
	 * @return {@link Optional<JobPosting>}
	 * @implSpec Id 값으로 조회 메서드 입니다.
	 */
	Optional<JobPosting> findById(Long id);

	/**
	 * @param jobPosting JobPosting 객체
	 * @return {@link JobPosting}
	 * @implSpec JobPosting 저장 메서드 입니다.
	 */
	JobPosting save(JobPosting jobPosting);

	/**
	 * @return {@link List<JobPosting>}
	 * @implSpec JobPosting 전체 조회 메서드 입니다.
	 */
	List<JobPosting> findAll();

	/**
	 * @return {@link Page<JobPostingPageResponse>}
	 * @implSpec JobPosting 페이징 동적 조회 메서드 입니다.
	 */
	Page<JobPostingPageResponse> findAll(JobPostingSearchCondition jobPostingSearchCondition,
		Pageable pageable);

	/**
	 * @return {@link JobPosting}
	 * @implSpec JobPosting 페이징 동적 조회 메서드 입니다.
	 */
	boolean existsById(Long jobPostingId);
}
