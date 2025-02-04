package com.backend.domain.jobskill.repository;

import com.backend.domain.jobskill.entity.JobSkill;
import java.util.Optional;

/**
 * JobSkillRepository
 * <p>JobSkill 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
public interface JobSkillRepository {

	/**
	 * @param id JobSkill id
	 * @return {@link Optional<JobSkill>}
	 * @implSpec Id 값으로 조회 메서드 입니다.
	 */
	Optional<JobSkill> findById(Long id);

	/**
	 * @param code JobSkill code
	 * @return {@link Optional<JobSkill>}
	 * @implSpec code 값으로 조회 메서드 입니다.
	 */
	Optional<JobSkill> findByCode(Integer code);

	/**
	 * @param jobSkill JobSkill 객체
	 * @return {@link JobSkill}
	 * @implSpec JobSkill 저장 메서드 입니다.
	 */
	JobSkill save(JobSkill jobSkill);

	/**
	 * @param name JobSkill name
	 * @return {@link Optional<JobSkill>}
	 * @implSpec name 값으로 조회 메서드 입니다.
	 */
	Optional<JobSkill> findByName(String name);
}
