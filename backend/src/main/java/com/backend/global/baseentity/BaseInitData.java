package com.backend.global.baseentity;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Profile("build")
@Component
@RequiredArgsConstructor
public class BaseInitData {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() throws InterruptedException {
        SiteUser admin = createAdmin();
    }

    private SiteUser createAdmin() throws InterruptedException {
        if (userRepository.count() > 0) return null;

        SiteUser siteUser = SiteUser.builder()
                .email("admin@admin.com")
                .name("admin")
                .password(passwordEncoder.encode("admin"))
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();

        SiteUser user1 = SiteUser.builder()
                .email("user@user.com")
                .name("user")
                .password(passwordEncoder.encode("user"))
                .userRole(UserRole.ROLE_USER.toString())
                .build();

        userRepository.save(siteUser);
        userRepository.save(user1);

        return siteUser;
    }

}
