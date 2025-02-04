package com.backend.domain.user;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.domain.user.dto.request.JobSkillRequest;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.annotation.CustomWithMock;
import com.backend.global.security.custom.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SiteUser testUser;
    private SiteUser otherUser;
    private JobSkill jobSkill1;
    private JobSkill jobSkill2;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(SiteUser.builder()
                .email("test@test.com")
                .password("password")
                .name("testUser")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        otherUser = userRepository.save(SiteUser.builder()
                .email("other@test.com")
                .password("password")
                .name("otherUser")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        jobSkill1 = jobSkillRepository.save(JobSkill.builder()
                .name("직무1")
                .code(1)
                .build());

        jobSkill2 = jobSkillRepository.save(JobSkill.builder()
                .name("직무2")
                .code(2)
                .build());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    @CustomWithMock
    void test1() throws Exception {
        testUser.modifyProfile("자기소개", "직업");
        jobSkill1.setSiteUser(testUser);
        testUser.getJobSkills().add(jobSkill1);

        mockMvc.perform(get("/api/v1/users/{user_id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value(testUser.getName()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.introduction").value("자기소개"))
                .andExpect(jsonPath("$.data.job").value("직업"))
                .andExpect(jsonPath("$.data.jobSkills[0].name").value("직무1"))
                .andExpect(jsonPath("$.data.jobSkills[0].code").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 비로그인 사용자")
    void test2() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(401))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 다른 사용자의 프로필 접근")
    @CustomWithMock
    void test3() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", otherUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4003))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 수정 성공")
    @CustomWithMock
    void test4() throws Exception {
        List<JobSkillRequest> jobSkills = List.of(
            new JobSkillRequest("직무1"),
            new JobSkillRequest("직무2")
        );

        UserModifyProfileRequest request = new UserModifyProfileRequest();
        request.setIntroduction("자기소개수정");
        request.setJob("직업수정");
        request.setJobSkills(jobSkills);

        mockMvc.perform(patch("/api/v1/users/{user_id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(new CustomUserDetails(testUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        SiteUser updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getIntroduction()).isEqualTo("자기소개수정");
        assertThat(updatedUser.getJob()).isEqualTo("직업수정");
        assertThat(updatedUser.getJobSkills()).hasSize(2);
        assertThat(updatedUser.getJobSkills().get(0).getName()).isEqualTo("직무1");
        assertThat(updatedUser.getJobSkills().get(1).getName()).isEqualTo("직무2");
    }

    @Test
    @DisplayName("프로필 수정 실패 - 비로그인 사용자")
    void test5() throws Exception {
        SiteUser siteUser = SiteUser.builder()
                .introduction("자기소개")
                .job("직업")
                .build();
        UserModifyProfileRequest request = new UserModifyProfileRequest(siteUser);

        mockMvc.perform(patch("/api/v1/users/{user_id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(401))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 수정 실패 - 다른 사용자의 프로필 수정 시도")
    @CustomWithMock
    void test6() throws Exception {
        SiteUser siteUser = SiteUser.builder()
                .introduction("자기소개")
                .job("직업")
                .build();
        UserModifyProfileRequest request = new UserModifyProfileRequest(siteUser);

        mockMvc.perform(patch("/api/v1/users/{user_id}", otherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4003))
                .andDo(MockMvcResultHandlers.print());

        SiteUser unchangedUser = userRepository.findById(otherUser.getId()).orElseThrow();
        assertThat(unchangedUser.getIntroduction()).isNull();
        assertThat(unchangedUser.getJob()).isNull();
    }

}
