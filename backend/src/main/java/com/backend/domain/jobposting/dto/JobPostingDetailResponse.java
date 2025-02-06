package com.backend.domain.jobposting.dto;

import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPostingStatus;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.domain.jobposting.entity.Salary;
import com.backend.domain.jobskill.dto.JobSkillResponse;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * JobPostingDetailResponse
 * <p>채용 공고 상세 조회 응답 객체 입니다.</p>
 *
 * @param id               id
 * @param subject          제목
 * @param url              공고 Url
 * @param postDate         작성 날짜
 * @param openDate         공개 날짜
 * @param closeDate        마감 날짜
 * @param companyName      회사 이름
 * @param companyLink      회사 Url
 * @param experienceLevel  직무 경력
 * @param requireEducate   학력
 * @param jobPostingStatus 공고 상태
 * @param salary           연봉
 * @param jobSkillList     직무 스킬
 * @param applyCnt         지원자 수
 * @author Kim Dong O
 */
public record JobPostingDetailResponse(
	Long id,
	String subject, //제목
	String url, //url

	ZonedDateTime postDate, //작성 날짜
	ZonedDateTime openDate, //공개 날짜
	ZonedDateTime closeDate, //마감 날짜

	String companyName, //회사 이름
	String companyLink,//회사 링크

	ExperienceLevel experienceLevel, //직무 경력

	RequireEducate requireEducate, //학력

	JobPostingStatus jobPostingStatus, //공고 상태

	Salary salary, //연봉

	List<JobSkillResponse> jobSkillList, //직무 스킬

	Long applyCnt, //지원자 수

	boolean isLike //관심 여부
) {

}
