package com.backend.domain.jobskill.repository;

import com.backend.domain.jobskill.entity.JobSkill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSkillJpaRepository extends JpaRepository<JobSkill, Long> {
	Optional<JobSkill> findByCode(Integer code);
	Optional<JobSkill> findByName(String name);
}
