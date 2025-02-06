package com.backend.domain.like;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.like.domain.LikeType;
import com.backend.domain.like.dto.LikeCreateRequest;
import com.backend.domain.like.entity.Like;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ApiV1LikeControllerTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	UserRepository userRepostiory;

	@Autowired
	LikeRepository likeRepository;

	@Value("${jwt.token.access-expiration}")
	long accessExpiration;

	String accessToken1;
	String accessToken2;

	@BeforeAll
	void setUp() {
		SiteUser givenSiteUser1 = userRepostiory.findByEmail("testEmail1@naver.com").get();
		CustomUserDetails givenCustomUserDetails1 = new CustomUserDetails(givenSiteUser1);
		accessToken1 = jwtUtil.createAccessToken(givenCustomUserDetails1, accessExpiration);

		SiteUser givenSiteUser2 = userRepostiory.findByEmail("testEmail1@naver.com").get();
		CustomUserDetails givenCustomUserDetails2 = new CustomUserDetails(givenSiteUser2);
		accessToken2 = jwtUtil.createAccessToken(givenCustomUserDetails2, accessExpiration);
	}

	@DisplayName("채용 공고 관심 등록 성공 테스트")
	@Order(1)
	@Test
	void save_job_posting_like_success() throws Exception {
		//given
		LikeCreateRequest givenRequest = LikeCreateRequest.builder()
			.likeType(LikeType.JOB_POSTING)
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isCreated());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(201))
			.andExpect(jsonPath("$.data.targetId").value(1))
			.andExpect(jsonPath("$.data.likeType").value(givenRequest.likeType().toString()));
	}

	@DisplayName("채용 공고 관심이 이미 존재할 때 실패 테스트")
	@Test
	void save_job_posting_like_fail() throws Exception {
		//given
		LikeCreateRequest givenRequest = LikeCreateRequest.builder()
			.likeType(LikeType.JOB_POSTING)
			.targetId(1L)
			.build();

		SiteUser givenSiteUser2 = userRepostiory.findByEmail("testEmail1@naver.com").get();

		likeRepository.save(Like.builder()
			.siteUser(givenSiteUser2)
			.likeType(LikeType.JOB_POSTING)
			.jobPosting(JobPosting.builder()
				.id(1L)
				.build())
			.build());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.ALREADY_LIKE.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.ALREADY_LIKE.getMessage()));
	}

	@DisplayName("관심 저장시 targetId가 null일 때 실패 테스트")
	@Test
	void save_like_target_id_null_fail() throws Exception {
		//given
		LikeCreateRequest givenRequest = LikeCreateRequest.builder()
			.likeType(LikeType.JOB_POSTING)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}

	@DisplayName("관심 저장시 likeType이 null일 때 실패 테스트")
	@Test
	void save_like_like_type_null_fail() throws Exception {
		//given
		LikeCreateRequest givenRequest = LikeCreateRequest.builder()
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}

	@DisplayName("관심 저장시 likeType이 존재하지 않는 값일 때 실패 테스트")
	@Test
	void save_like_like_type_not_valid_fail() throws Exception {
		//given
		Map<String, Object> param = new HashMap<>();
		param.put("targetId", 1L);
		param.put("likeType", "testLike");

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(param)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}
}
