package com.backend.domain.jobskill.constant;

import lombok.Getter;

/**
 * JobSkillConstant
 * <p>JobSkill 관련 상수를 정의한 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
public enum JobSkillConstant {

	//Redis에 저장될 JobSkill Key 값
	JOB_SKILL_REDIS_KEY("job_skill_key:");

	private final String key;

	JobSkillConstant(String key) {
		this.key = key;
	}
}
