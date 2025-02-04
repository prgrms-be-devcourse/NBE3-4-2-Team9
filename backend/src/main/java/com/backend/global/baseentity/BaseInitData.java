package com.backend.global.baseentity;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillJpaRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Profile({"build", "dev"})
@Component
@RequiredArgsConstructor
public class BaseInitData {

    private final UserRepository userRepository;
    private final JobSkillJpaRepository jobSkillRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() throws InterruptedException {
        SiteUser admin = createAdmin();
        SiteUser user = createUser();
        List<JobSkill> jobSkills = createJobSkill();
    }

    private SiteUser createAdmin() throws InterruptedException {
        if (userRepository.count() > 0) return null;

        SiteUser siteUser = SiteUser.builder()
                .email("admin@admin.com")
                .name("admin")
                .password(passwordEncoder.encode("admin"))
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();

        userRepository.save(siteUser);

        return siteUser;
    }

    private SiteUser createUser() throws InterruptedException {
        if (userRepository.count() > 0) return null;

        SiteUser user1 = SiteUser.builder()
                .email("user1@user.com")
                .name("user1")
                .password(passwordEncoder.encode("user"))
                .userRole(UserRole.ROLE_USER.toString())
                .build();

        userRepository.save(user1);

        return user1;
    }

    private List<JobSkill> createJobSkill() throws InterruptedException {
        if (jobSkillRepository.count() > 0) return null;

        List<JobSkill> jobSkills = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            JobSkill skill = JobSkill.builder()
                    .name("직무" + i)
                    .code(i)
                    .build();
            jobSkillRepository.save(skill);
            jobSkills.add(skill);
        }

        return jobSkills;
    }

}
