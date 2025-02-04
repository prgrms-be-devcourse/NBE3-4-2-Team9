package com.backend.domain.user.dto.request;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.user.entity.SiteUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserModifyProfileRequest {

    private String introduction;

    private String job;

    private List<JobSkillRequest> jobSkills;

    public UserModifyProfileRequest(SiteUser siteUser) {
        this.introduction = siteUser.getIntroduction();
        this.job = siteUser.getJob();
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setJobSkills(List<JobSkillRequest> jobSkills) {
        this.jobSkills = jobSkills;
    }

}
