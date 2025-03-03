package com.backend.domain.recruitmentUser.repository;

import com.backend.domain.recruitmentUser.entity.RecruitmentUser;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 모집 지원자 관련 데이터 처리 리포지토리
 */
public interface RecruitmentUserRepository extends JpaRepository<RecruitmentUser, Long> {

	/**
	 * 특정 사용자가 특정 모집 게시글에 지원한 내역 조회
	 * - Post, SiteUser 데이터, JobSkills 정보를 즉시 로딩
	 *
	 * @param postId 모집 게시글 ID
	 * @param userId 사용자 ID
	 * @return 모집 지원 내역 (존재하지 않을 경우 빈 Optional 반환)
	 */
	@Query("""
		    SELECT DISTINCT ru FROM RecruitmentUser ru
		    JOIN FETCH ru.post p
		    JOIN FETCH ru.siteUser su
		    JOIN FETCH su.jobSkills
		    WHERE p.postId = :postId AND su.id = :userId
		""")
	//TODO 언더바 추후 제거할 것
	Optional<RecruitmentUser> findByPost_PostIdAndSiteUser_Id(
		@Param("postId") Long postId,
		@Param("userId") Long userId
	);

	/**
	 * 특정 모집 게시글에 대해 지정된 상태의 모집 지원자 목록 조회
	 * - Post 및 SiteUser, JobSkills 정보 즉시 로딩
	 *
	 * @param postId 모집 게시글 ID
	 * @param status 조회할 모집 지원 상태 (예: APPLIED, ACCEPTED, REJECTED 등)
	 * @param pageable 페이지네이션 정보
	 * @return 해당 상태의 모집 지원자 목록 (페이징 결과)
	 */
	@Query("""
		    SELECT ru FROM RecruitmentUser ru
		    JOIN FETCH ru.post p
		    JOIN FETCH ru.siteUser su
		    JOIN FETCH su.jobSkills
		    WHERE p.postId = :postId AND ru.status = :status
		""")
	Page<RecruitmentUser> findAllByPost_PostIdAndStatus(
		@Param("postId") Long postId,
		@Param("status") RecruitmentUserStatus status,
		Pageable pageable
	);

	/**
	 * 특정 사용자가 지정된 상태로 모집된 게시글 목록을 페이지네이션하여 조회
	 * - Post 및 SiteUser, JobSkills 정보 즉시 로딩
	 *
	 * @param userId   사용자 ID
	 * @param status   모집 지원 상태 (예: APPLIED, ACCEPTED, REJECTED 등)
	 * @param pageable 페이지네이션 정보
	 * @return 모집된 게시글 목록 (페이징 결과)
	 */
	@Query("""
		    SELECT ru FROM RecruitmentUser ru
		    JOIN FETCH ru.post p
		    JOIN FETCH ru.siteUser su
		    JOIN FETCH su.jobSkills
		    WHERE su.id = :userId AND ru.status = :status
		""")
	Page<RecruitmentUser> findAllBySiteUser_IdAndStatus(
		@Param("userId") Long userId,
		@Param("status") RecruitmentUserStatus status,
		Pageable pageable
	);

	/**
	 * 특정 사용자가 특정 모집 게시글에 승인된 상태로 존재하는지 검증
	 *
	 * @param postId 모집 게시글 ID
	 * @param userId 사용자 ID
	 * @return 승인 유저인지 검증
	 */
	@Query("""
		    SELECT CASE WHEN COUNT(ru) > 0 THEN TRUE ELSE FALSE END
		    FROM RecruitmentUser ru
		    WHERE ru.post.postId = :postId
		      AND ru.siteUser.id = :userId
		      AND ru.status = com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus.ACCEPTED
		""")
	boolean existsRecruitmentUserByPost_PostIdAndSiteUser_Id(
		@Param(value = "postId") Long postId,
		@Param(value = "userId") Long userId
	);


    /**
     * 특정 게시글에서 모집이 종료(CLOSED)된 상태에서 승인된(ACCEPTED) 모집 지원자 목록 조회
     *
     * @param postId 게시글 ID
     * @return 모집 종료(CLOSED)된 게시글에서 승인된 모집 지원자 목록
     */
    @Query("SELECT ru FROM RecruitmentUser ru " +
        "JOIN FETCH ru.post p " +
        "WHERE p.postId = :postId " +
        "AND p.recruitmentStatus = com.backend.domain.post.entity.RecruitmentStatus.CLOSED " +
        "AND ru.status = com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus.ACCEPTED")
    List<RecruitmentUser> findAcceptedByClosed(@Param("postId") Long postId);

    /**
     * 특정 게시글에 대해 승인된(ACCEPTED) 모집 지원자 수 조회
     *
     * @param postId 게시글 ID
     * @return 승인된 모집 지원자 수
     */
    @Query("SELECT COUNT(ru) FROM RecruitmentUser ru " +
        "WHERE ru.post.postId = :postId AND ru.status = com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus.ACCEPTED")
    int countAcceptedByPostId(@Param("postId") Long postId);

}
