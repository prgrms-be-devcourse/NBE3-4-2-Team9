package com.backend.domain.jobskill.repository;

import com.backend.domain.jobskill.entity.JobSkill;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobSkillRepositoryImpl implements JobSkillRepository{
	private final JobSkillJpaRepository jobSkillJpaRepository;

	@Override
	public Optional<JobSkill> findById(Long id) {
		return jobSkillJpaRepository.findById(id);
	}

	@Override
	public Optional<JobSkill> findByCode(Integer code) {
		return jobSkillJpaRepository.findByCode(code);
	}

	@Override
	public JobSkill save(JobSkill jobSkill) {
		return jobSkillJpaRepository.save(jobSkill);
	}

	@Override
	public Optional<JobSkill> findByName(String name) { return jobSkillJpaRepository.findByName(name); }
}
