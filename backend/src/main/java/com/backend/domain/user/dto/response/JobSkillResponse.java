package com.backend.domain.user.dto.response;

import com.backend.domain.jobskill.entity.JobSkill;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobSkillResponse {

    private String name;
    private Integer code;

    public JobSkillResponse(JobSkill jobSkill) {
        this.name = jobSkill.getName();
        this.code = jobSkill.getCode();
    }

}
