package com.backend.domain.jobposting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.jobposting.dto.JobPostingDetailResponse;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ApiV1JobPostingControllerTest {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JobPostingRepository jobPostingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	JwtUtil jwtUtil;

	@Value("${jwt.token.access-expiration}")
	long accessExpiration;

	SiteUser givenSiteUser1;
	SiteUser givenSiteUser2;

	String accessToken1;
	String accessToken2;

	@BeforeAll
	void setUp() {
		givenSiteUser1 = userRepository.findByEmail("testEmail1@naver.com").get();
		CustomUserDetails givenCustomUserDetails1 = new CustomUserDetails(givenSiteUser1);
		accessToken1 = jwtUtil.createAccessToken(givenCustomUserDetails1, accessExpiration);

		givenSiteUser2 = userRepository.findByEmail("testEmail3@naver.com").get();
		CustomUserDetails givenCustomUserDetails2 = new CustomUserDetails(givenSiteUser2);
		accessToken2 = jwtUtil.createAccessToken(givenCustomUserDetails2, accessExpiration);
	}

	@DisplayName("기본 페이징 조회 성공 테스트")
	@Test
	void findAll_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(10))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.totalPages").value(2));
	}

	@DisplayName("페이징 경력 조건 신입 조회 성공 테스트")
	@Test
	void findAll_experience_level_1_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.queryParam("experienceLevel", "1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(7))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(1))
			.andExpect(jsonPath("$.data.totalPages").value(1));
	}

	@DisplayName("페이징 경력 조건 조회 성공 테스트")
	@Test
	void findAll_experience_level_2_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.queryParam("experienceLevel", "2")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(1));
	}

	@DisplayName("페이징 연봉 조건 조회 성공 테스트")
	@Test
	void findAll_salary_code_99_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.queryParam("salaryCode", "99")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(7))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].salary.code").value(99))
			.andExpect(jsonPath("$.data.totalPages").value(1));
	}

	@DisplayName("페이징 조회 페이지 사이즈 음수 실패 테스트")
	@Test
	void findAll_page_size_negative_fail() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.queryParam("pageSize", "-1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("pageSize"))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andDo(print());
	}

	@DisplayName("페이징 조회 페이지 번호 음수 실패 테스트")
	@Test
	void findAll_page_num_negative_fail() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.queryParam("pageNum", "-1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("pageNum"))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andDo(print());
	}

	@DisplayName("채용 공고 단건 조회 성공 테스트")
	@Test
	void findDetailById_success() throws Exception {
		//given
		JobPostingDetailResponse givenJobPosting = jobPostingRepository
			.findDetailById(1L, givenSiteUser1.getId()).get();

		//when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/job-posting/{id}", givenJobPosting.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1));

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			//공고 ID 검증
			.andExpect(jsonPath("$.data.id").value(givenJobPosting.id()))
			//공고 제목 검증
			.andExpect(jsonPath("$.data.subject").value(givenJobPosting.subject()))
			//공고 URL 검증
			.andExpect(jsonPath("$.data.url").value(givenJobPosting.url()))
			//공고 날짜 값 검증
			.andExpect(jsonPath("$.data.postDate")
				.value(givenJobPosting.postDate().format(FORMATTER)))
			.andExpect(jsonPath("$.data.openDate")
				.value(givenJobPosting.openDate().format(FORMATTER)))
			.andExpect(jsonPath("$.data.closeDate")
				.value(givenJobPosting.closeDate().format(FORMATTER)))
			//회사 검증
			.andExpect(jsonPath("$.data.companyName").value(givenJobPosting.companyName()))
			.andExpect(jsonPath("$.data.companyLink").value(givenJobPosting.companyLink()))
			//경력 검증
			.andExpect(jsonPath("$.data.experienceLevel.code")
				.value(givenJobPosting.experienceLevel().getCode()))
			.andExpect(jsonPath("$.data.experienceLevel.min")
				.value(givenJobPosting.experienceLevel().getMin()))
			.andExpect(jsonPath("$.data.experienceLevel.max")
				.value(givenJobPosting.experienceLevel().getMax()))
			.andExpect(jsonPath("$.data.experienceLevel.name")
				.value(givenJobPosting.experienceLevel().getName()))
			//학력 검증
			.andExpect(jsonPath("$.data.requireEducate.code")
				.value(givenJobPosting.requireEducate().getCode()))
			.andExpect(jsonPath("$.data.requireEducate.name")
				.value(givenJobPosting.requireEducate().getName()))
			//공고 상태 검증
			.andExpect(jsonPath("$.data.jobPostingStatus")
				.value(givenJobPosting.jobPostingStatus().toString()))
			//JobSkillList 검증
			.andExpect(jsonPath("$.data.jobSkillList[0].name")
				.value(givenJobPosting.jobSkillList().getFirst().name()))
			.andExpect(jsonPath("$.data.jobSkillList[0].code")
				.value(givenJobPosting.jobSkillList().getFirst().code()))
			.andExpect(jsonPath("$.data.jobSkillList[1].name")
				.value(givenJobPosting.jobSkillList().get(1).name()))
			.andExpect(jsonPath("$.data.jobSkillList[1].code")
				.value(givenJobPosting.jobSkillList().get(1).code()))
			//지원자 수 검증
			.andExpect(jsonPath("$.data.applyCnt").value(givenJobPosting.applyCnt()))
			//추천 수 검증
			.andExpect(jsonPath("$.data.voterCount").value(givenJobPosting.voterCount()))
			//추천 여부 검증
			.andExpect(jsonPath("$.data.isVoter").value(givenJobPosting.isVoter()));
	}

	@DisplayName("채용 공고 단건 조회 사용자 미추천 검증 성공 테스트")
	@Test
	void findDetailById_isVoter_false_success() throws Exception {
		//given
		JobPostingDetailResponse givenJobPosting = jobPostingRepository
			.findDetailById(1L, givenSiteUser2.getId()).get();

		//when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/job-posting/{id}", givenJobPosting.id())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2));

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
			//공고 ID 검증
			.andExpect(jsonPath("$.data.id").value(givenJobPosting.id()))
			//공고 제목 검증
			.andExpect(jsonPath("$.data.subject").value(givenJobPosting.subject()))
			//공고 URL 검증
			.andExpect(jsonPath("$.data.url").value(givenJobPosting.url()))
			//공고 날짜 값 검증
			.andExpect(jsonPath("$.data.postDate")
				.value(givenJobPosting.postDate().format(FORMATTER)))
			.andExpect(jsonPath("$.data.openDate")
				.value(givenJobPosting.openDate().format(FORMATTER)))
			.andExpect(jsonPath("$.data.closeDate")
				.value(givenJobPosting.closeDate().format(FORMATTER)))
			//회사 검증
			.andExpect(jsonPath("$.data.companyName").value(givenJobPosting.companyName()))
			.andExpect(jsonPath("$.data.companyLink").value(givenJobPosting.companyLink()))
			//경력 검증
			.andExpect(jsonPath("$.data.experienceLevel.code")
				.value(givenJobPosting.experienceLevel().getCode()))
			.andExpect(jsonPath("$.data.experienceLevel.min")
				.value(givenJobPosting.experienceLevel().getMin()))
			.andExpect(jsonPath("$.data.experienceLevel.max")
				.value(givenJobPosting.experienceLevel().getMax()))
			.andExpect(jsonPath("$.data.experienceLevel.name")
				.value(givenJobPosting.experienceLevel().getName()))
			//학력 검증
			.andExpect(jsonPath("$.data.requireEducate.code")
				.value(givenJobPosting.requireEducate().getCode()))
			.andExpect(jsonPath("$.data.requireEducate.name")
				.value(givenJobPosting.requireEducate().getName()))
			//공고 상태 검증
			.andExpect(jsonPath("$.data.jobPostingStatus")
				.value(givenJobPosting.jobPostingStatus().toString()))
			//JobSkillList 검증
			.andExpect(jsonPath("$.data.jobSkillList[0].name")
				.value(givenJobPosting.jobSkillList().getFirst().name()))
			.andExpect(jsonPath("$.data.jobSkillList[0].code")
				.value(givenJobPosting.jobSkillList().getFirst().code()))
			.andExpect(jsonPath("$.data.jobSkillList[1].name")
				.value(givenJobPosting.jobSkillList().get(1).name()))
			.andExpect(jsonPath("$.data.jobSkillList[1].code")
				.value(givenJobPosting.jobSkillList().get(1).code()))
			//지원자 수 검증
			.andExpect(jsonPath("$.data.applyCnt").value(givenJobPosting.applyCnt()))
			//추천 수 검증
			.andExpect(jsonPath("$.data.voterCount").value(givenJobPosting.voterCount()))
			//추천 여부 검증
			.andExpect(jsonPath("$.data.isVoter").value(givenJobPosting.isVoter()));
	}

	@DisplayName("채용 공고 단건 조회 실패 테스트")
	@Test
	void findDetailById_not_found_fail() throws Exception {
		//given
		Long givenId = 999L;

		//when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/job-posting/{id}", givenId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2));

		//then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.JOB_POSTING_NOT_FOUND.getCode()))
			.andExpect(
				jsonPath("$.message").value(GlobalErrorCode.JOB_POSTING_NOT_FOUND.getMessage()));
	}

	@DisplayName("추천한 채용 공고 조회 성공 테스트")
	@Test
	void findAllVoter_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting/voter")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + accessToken1));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(3))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.totalPages").value(1));
	}

}