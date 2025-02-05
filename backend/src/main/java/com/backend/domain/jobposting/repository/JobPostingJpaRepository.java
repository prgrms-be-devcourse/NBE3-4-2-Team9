package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.entity.JobPosting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingJpaRepository extends JpaRepository<JobPosting, Long> {
	@Query("select j from JobPosting j join fetch j.jobSkillList where j.id = :id")
	Optional<JobPosting> findById(@Param("id") Long id);
	boolean existsById(Long jobPostingId);
}
