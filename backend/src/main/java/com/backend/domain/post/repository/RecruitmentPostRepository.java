package com.backend.domain.post.repository;

import com.backend.domain.post.entity.RecruitmentPost;
import com.backend.domain.post.entity.RecruitmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

    List<RecruitmentPost> findByRecruitmentStatus(RecruitmentStatus recruitmentStatus);
}
