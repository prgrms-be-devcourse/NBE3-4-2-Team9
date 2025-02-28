package com.backend.global.baseentity;

import com.backend.domain.category.domain.CategoryName;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.entity.JobPostingJobSkill;
import com.backend.domain.jobposting.entity.JobPostingStatus;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.domain.jobposting.entity.Salary;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.jobskill.constant.JobSkillConstant;
import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillJpaRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.redis.repository.RedisRepository;
import java.time.ZonedDateTime;
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
	private final JobPostingRepository jobPostingRepository;
	private final PasswordEncoder passwordEncoder;
	private final CategoryRepository categoryRepository;
	private final RedisRepository redisRepository;

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	void init() throws InterruptedException {
		createAdminAndUser();
//        createJobPosting();
		createRedisJobSkill();
		createCategory();
	}

	private void createAdminAndUser() throws InterruptedException {
        if (userRepository.count() > 0) {
            return;
        }

		List<SiteUser> users = new ArrayList<>();

		SiteUser admin = SiteUser.builder()
			.email("admin@admin.com")
			.name("admin")
			.password(passwordEncoder.encode("admin"))
			.userRole(UserRole.ROLE_ADMIN.toString())
			.build();
		userRepository.save(admin);
		users.add(admin);

		SiteUser user1 = SiteUser.builder()
			.email("user1@user.com")
			.name("user1")
			.password(passwordEncoder.encode("user"))
			.userRole(UserRole.ROLE_USER.toString())
			.build();
		userRepository.save(user1);
		users.add(user1);

		SiteUser user2 = SiteUser.builder()
			.email("user2@user.com")
			.name("user2")
			.password(passwordEncoder.encode("user"))
			.userRole(UserRole.ROLE_USER.toString())
			.build();
		userRepository.save(user2);
		users.add(user2);
	}

	private void createRedisJobSkill() {
		jobSkillRepository.findAll().forEach(jobSkill -> {
			String redisKey = JobSkillConstant.JOB_SKILL_REDIS_KEY.getKey() + jobSkill.getCode();
			boolean hasKey = redisRepository.hasKey(redisKey);

			if (!hasKey) {
				redisRepository.save(redisKey, jobSkill.getId());
			}
		});
	}

	private void createJobPosting() {

		JobSkill jobSkill1 = jobSkillRepository.findById(1L).get();
		JobSkill jobSkill2 = jobSkillRepository.findById(2L).get();
		JobSkill jobSkill3 = jobSkillRepository.findById(3L).get();

		for (int i = 1; i < 15; i++) {
			JobPosting jobPosting = JobPosting.builder()
				.url("testUrl")
				.salary(Salary.builder().code(22).name("1억원 이상").build())
				.jobPostingStatus(JobPostingStatus.ACTIVE)
				.companyName("testCompany")
				.subject("testSubject")
				.requireEducate(RequireEducate.builder()
					.code(0).name("학력 무관").build())
				.postDate(ZonedDateTime.now())
				.closeDate(ZonedDateTime.now().plusDays(1))
				.openDate(ZonedDateTime.now())
				.experienceLevel(ExperienceLevel.builder()
					.code(1).name("신입").build())
				.companyLink("testCompanyLink")
				.applyCnt((long) i)
				.voterList(null)
				.subject("testSubject" + i)
				.jobId((long) i)
				.build();

			JobPostingJobSkill jobPostingJobSkill1 = JobPostingJobSkill.builder()
				.jobSkill(jobSkill1)
				.jobPosting(jobPosting)
				.build();

			JobPostingJobSkill jobPostingJobSkill2 = JobPostingJobSkill.builder()
				.jobSkill(jobSkill1)
				.jobPosting(jobPosting)
				.build();

			jobPosting.getJobPostingJobSkillList().add(jobPostingJobSkill1);
			jobPosting.getJobPostingJobSkillList().add(jobPostingJobSkill2);

            jobPostingRepository.save(jobPosting);
		}
	}

	private void createCategory() {
		if (categoryRepository.findAll().size() > 0) {
			return;
		}

		List<Category> categories = new ArrayList<>();

		Category freeBoard = Category.builder()
				.name(CategoryName.FREE.getValue())
				.build();
		categories.add(freeBoard);

		Category recruitmentBoard = Category.builder()
				.name(CategoryName.RECRUITMENT.getValue())
				.build();
		categories.add(recruitmentBoard);

		categoryRepository.saveAll(categories);
	}

}
