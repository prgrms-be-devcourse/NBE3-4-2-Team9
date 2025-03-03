package com.backend.domain.post.repository.recruitment;

import com.backend.domain.post.entity.RecruitmentPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentPostJpaRepository extends JpaRepository<RecruitmentPost, Long> {
	@Query("""
SELECT rp 
FROM RecruitmentPost rp 
LEFT JOIN FETCH rp.author 
LEFT JOIN FETCH rp.jobPosting 
WHERE rp.postId = :postId 
""")
	Optional<RecruitmentPost> findByIdFetch(Long postId);
}
