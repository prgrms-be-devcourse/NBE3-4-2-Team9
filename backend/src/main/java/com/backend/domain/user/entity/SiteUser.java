package com.backend.domain.user.entity;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SiteUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;    // 유저 고유 식별 id

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "")
    private String email;   // 사용자 이메일

    @Column(name = "password", nullable = false)
    private String password;    // 사용자 비밀번호

    @Column(name = "name", nullable = false)
    private String name;    // 사용자 이름

    @Column(name = "introduction", nullable = true)
    private String introduction;    // 사용자 자기소개

    @Column(name = "job", nullable = true)
    private String job;     // 사용자 직무

    @Column(name = "user_role", nullable = false)
    private String userRole;    // 사용자 권한

    private String kakaoId;     // 카카오 고유 식별 id

    private String profileImg;  // 카카오 프로필 이미지 URL

    @Builder.Default
    @OneToMany(mappedBy = "siteUser", cascade = CascadeType.REMOVE)
    private List<JobSkill> jobSkills = new ArrayList<>();

    public void modifyProfile(String introduction, String job) {
        if (introduction != null) this.introduction = introduction;
        if (job != null) this.job = job;
    }

    public SiteUser update(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
        return this;
    }

}
