package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.dto.JobPostingDetailResponse;
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
	 * @param jobPostingId JobPosting id
	 * @param siteUserId   SiteUser id
	 * @return {@link Optional<JobPostingDetailResponse>}
	 * @implSpec jobPostingId, siteUserId 값으로 조회 메서드 입니다.
	 */
	Optional<JobPostingDetailResponse> findDetailById(Long jobPostingId, Long siteUserId);


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

	List<JobPosting> saveAll(List<JobPosting> publicDataList);

	List<Long> findIdsAll();

	/**
	 * @param jobPostingSearchCondition 검색 조건 객체
	 * @param pageable pageable
	 * @return {@link Page<JobPostingPageResponse>}
	 * @implSpec JobPosting 페이징 동적 조회 메서드 입니다.
	 */
	Page<JobPostingPageResponse> findAll(JobPostingSearchCondition jobPostingSearchCondition,
		Pageable pageable);

	/**
	 * @param jobPostingId 채용 공고 ID
	 * @return boolean - 데이터가 있으면 true, 없으면 false를 반환합니다.
	 * @implSpec JobPosting 중복 체크 메서드 입니다.
	 */
	boolean existsById(Long jobPostingId);

	/**
	 * @param jobPostingSearchCondition 검색 조건 객체
	 * @param siteUserId 추천한 사용자 ID
	 * @param pageable pageable
	 * @return {@link Page<JobPostingPageResponse>}
	 * @implSpec 사용자가 추천한 게시글을 조회하는 메서드 입니다.
	 */
	Page<JobPostingPageResponse> findAllVoter(JobPostingSearchCondition jobPostingSearchCondition,
		Long siteUserId, Pageable pageable);
}
