package com.backend.domain.user.service;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.domain.user.dto.request.JobSkillRequest;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JobSkillRepository jobSkillRepository;

    public SiteUser getUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public void modifyUser(SiteUser siteUser, UserModifyProfileRequest req) {
        SiteUser user = getUserById(siteUser.getId());
        
        if (req.getJobSkills() != null) {
            user.getJobSkills().clear();

            req.getJobSkills().forEach(jobSkillReq -> {
                JobSkill jobSkill = jobSkillRepository.findByName(jobSkillReq.getName())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_JOB_SKILL));
                user.getJobSkills().add(jobSkill);
            });
        }

        user.modifyProfile(req.getIntroduction(), req.getJob());
    }

}
