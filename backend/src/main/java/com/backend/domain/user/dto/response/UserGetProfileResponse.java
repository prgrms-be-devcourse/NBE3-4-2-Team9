package com.backend.domain.user.dto.response;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.user.dto.request.JobSkillRequest;
import com.backend.domain.user.entity.SiteUser;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserGetProfileResponse {

    private String name;

    private String email;

    private String introduction;

    private String job;

    private List<JobSkillResponse> jobSkills;

    private String profileImg;

    public UserGetProfileResponse(SiteUser siteUser) {
        this.name = siteUser.getName();
        this.email = siteUser.getEmail();
        this.introduction = siteUser.getIntroduction();
        this.job = siteUser.getJob();
        this.jobSkills = siteUser.getJobSkills() != null ?
                siteUser.getJobSkills().stream().map(JobSkillResponse::new).collect(Collectors.toList()) :
                null;
        this.profileImg = siteUser.getProfileImg();
    }

}
