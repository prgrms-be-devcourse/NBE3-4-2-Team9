package com.backend.domain.jobposting.repository;

import static com.backend.domain.jobposting.entity.QJobPosting.jobPosting;
import static com.backend.domain.voter.entity.QVoter.voter;
import static com.backend.domain.jobposting.entity.QJobPostingJobSkill.jobPostingJobSkill;
import static com.backend.domain.voter.entity.QVoter.voter;

import com.backend.domain.jobposting.dto.JobPostingDetailResponse;
import com.backend.domain.jobposting.dto.JobPostingPageResponse;
import com.backend.domain.jobposting.dto.QJobPostingDetailResponse;
import com.backend.domain.jobposting.dto.QJobPostingPageResponse;
import com.backend.domain.jobposting.util.JobPostingSearchCondition;
import com.backend.domain.jobskill.dto.JobSkillResponse;
import com.backend.domain.jobskill.dto.QJobSkillResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * JobPostingQueryRepository
 * <p>채용 공고 조회 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JobPostingQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<JobPostingPageResponse> findAll(JobPostingSearchCondition jobPostingSearchCondition,
		Pageable pageable) {

		//조회 로직
		List<JobPostingPageResponse> content = queryFactory.select(
				new QJobPostingPageResponse(jobPosting.id, jobPosting.subject,
					jobPosting.openDate, jobPosting.closeDate, jobPosting.experienceLevel,
					jobPosting.requireEducate, jobPosting.jobPostingStatus, jobPosting.salary,
					jobPosting.applyCnt))
			.from(jobPosting)
			.where(getSubjectContains(jobPostingSearchCondition.kw()),
				getExperienceLevelEq(jobPostingSearchCondition.experienceLevel()),
				getRequireEducateCode(jobPostingSearchCondition.requireEducateCode()),
				getSalaryCodeBetween(jobPostingSearchCondition.salaryCode()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		//카운트 쿼리
		JPAQuery<Long> countQuery = queryFactory.select(jobPosting.count())
			.from(jobPosting)
			.where(getSubjectContains(jobPostingSearchCondition.kw()),
				getExperienceLevelEq(jobPostingSearchCondition.experienceLevel()),
				getRequireEducateCode(jobPostingSearchCondition.requireEducateCode()),
				getSalaryCodeBetween(jobPostingSearchCondition.salaryCode()));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public Optional<JobPostingDetailResponse> findDetailById(Long jobPostingId, Long siteUserId) {
		BooleanExpression siteUserVoted = JPAExpressions
			.selectOne()
			.from(voter)
			.leftJoin(voter.jobPosting)
			.where(
				voter.jobPosting.id.eq(jobPostingId)
			)
			.exists();

		List<JobSkillResponse> jobSkillResponses = queryFactory.select(
				new QJobSkillResponse(jobPostingJobSkill.jobSkill.name,
					jobPostingJobSkill.jobSkill.code))
			.from(jobPostingJobSkill)
			.leftJoin(jobPostingJobSkill.jobPosting)
			.leftJoin(jobPostingJobSkill.jobSkill)
			.where(jobPostingJobSkill.jobPosting.id.eq(jobPostingId))
			.fetch();

		JobPostingDetailResponse jobPostingDetailResponse = queryFactory.selectDistinct(
				new QJobPostingDetailResponse(jobPosting.id, jobPosting.subject, jobPosting.url,
					jobPosting.postDate, jobPosting.openDate, jobPosting.closeDate,
					jobPosting.companyName, jobPosting.companyLink, jobPosting.experienceLevel,
					jobPosting.requireEducate, jobPosting.jobPostingStatus, jobPosting.salary,
					Expressions.constant(jobSkillResponses),
					jobPosting.applyCnt, voter.countDistinct(), siteUserVoted))
			.from(jobPosting)
			.leftJoin(jobPosting.voterList, voter)
			.groupBy(jobPosting.id, jobPosting.postDate, jobPosting.openDate, jobPosting.closeDate,
				jobPosting.companyName, jobPosting.companyLink, jobPosting.experienceLevel,
				jobPosting.requireEducate, jobPosting.jobPostingStatus, jobPosting.salary,
				jobPosting.applyCnt)
			.where(jobPosting.id.eq(jobPostingId))
			.fetchOne();

		return Optional.ofNullable(jobPostingDetailResponse);
	}

	public Page<JobPostingPageResponse> findAllVoter(JobPostingSearchCondition jobPostingSearchCondition,
		Long siteUserId, Pageable pageable) {

		//조회 로직
		List<JobPostingPageResponse> content = queryFactory.select(
				new QJobPostingPageResponse(jobPosting.id, jobPosting.subject,
					jobPosting.openDate, jobPosting.closeDate, jobPosting.experienceLevel,
					jobPosting.requireEducate, jobPosting.jobPostingStatus, jobPosting.salary,
					jobPosting.applyCnt))
			.from(jobPosting)
			.leftJoin(jobPosting.voterList, voter)
			.where(getSubjectContains(jobPostingSearchCondition.kw()),
				getExperienceLevelEq(jobPostingSearchCondition.experienceLevel()),
				getRequireEducateCode(jobPostingSearchCondition.requireEducateCode()),
				getSalaryCodeBetween(jobPostingSearchCondition.salaryCode()),
				getVoterSiteUserEq(siteUserId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		//카운트 쿼리
		JPAQuery<Long> countQuery = queryFactory.select(jobPosting.count())
			.from(jobPosting)
			.where(getSubjectContains(jobPostingSearchCondition.kw()),
				getExperienceLevelEq(jobPostingSearchCondition.experienceLevel()),
				getRequireEducateCode(jobPostingSearchCondition.requireEducateCode()),
				getSalaryCodeBetween(jobPostingSearchCondition.salaryCode()),
				getVoterSiteUserEq(siteUserId));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	/**
	 * 정렬할 필드와 정렬 방식을 OrderSpecifier로 반환합니다.
	 *
	 * @param jobPostingSearchCondition
	 * @return {@link OrderSpecifier<?>}
	 */
	private OrderSpecifier<?> getOrderBy(JobPostingSearchCondition jobPostingSearchCondition) {
		// 기본 정렬 방식 설정
		Order queryOrder =
			Order.ASC.toString().equalsIgnoreCase(jobPostingSearchCondition.order()) ?
				Order.ASC : Order.DESC;

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"applyCnt", jobPosting.applyCnt,
			"openDate", jobPosting.openDate
		);

		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(jobPostingSearchCondition.sort()) && fieldMap.containsKey(
				jobPostingSearchCondition.sort())
				? fieldMap.get(jobPostingSearchCondition.sort())
				: jobPosting.openDate;

		return new OrderSpecifier<>(queryOrder, sortField);
	}

	/**
	 * 연봉 코드 조건식을 반환합니다.
	 *
	 * @param salaryCode 연봉 코드
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getSalaryCodeBetween(Integer salaryCode) {
		if (salaryCode == null) {
			//salaryCode가 null인지 체크
			return null;
		} else if (salaryCode == 0 || salaryCode >= 99) {
			//salaryCode가 0이거나 99와 같거나 큰지 체크
			return jobPosting.salary.code.eq(salaryCode);
		} else {
			//위 조건 해당되지 않는다면 between
			return jobPosting.salary.code.between(salaryCode, 22);
		}
	}

	/**
	 * 학력 코드 조건식을 반환합니다.
	 *
	 * @param requireEducateCode 학력 코드
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getRequireEducateCode(Integer requireEducateCode) {
		return requireEducateCode != null ?
			jobPosting.requireEducate.code.eq(requireEducateCode) : null;
	}

	/**
	 * 경력 코드 조건식을 반환합니다.
	 *
	 * @param experienceLevel 경력 코드
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getExperienceLevelEq(Integer experienceLevel) {
		return experienceLevel != null ?
			jobPosting.experienceLevel.code.eq(experienceLevel) : null;
	}

	/**
	 * 키워드 조건식을 반환합니다.
	 *
	 * @param kw
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getSubjectContains(String kw) {
		return StringUtils.hasText(kw) ? jobPosting.subject.contains(kw) : null;
	}

	/**
	 * 추천한 사용자의 조건식을 반환합니다.
	 *
	 * @param siteUserId
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getVoterSiteUserEq(Long siteUserId) {
		return voter.siteUser.id.eq(siteUserId);
	}
}