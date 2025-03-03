package com.backend.domain.post.repository.recruitment;

import static com.backend.domain.post.entity.QRecruitmentPost.*;
import static com.backend.domain.recruitmentUser.entity.QRecruitmentUser.*;
import static com.backend.domain.voter.entity.QVoter.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.post.dto.QRecruitmentPostResponse;
import com.backend.domain.post.dto.RecruitmentPostResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RecruitmentPostQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<RecruitmentPostResponse> findPostResponseById(Long postId, Long siteUserId) {
		RecruitmentPostResponse postResponse = queryFactory.selectDistinct(
				new QRecruitmentPostResponse(recruitmentPost.postId, recruitmentPost.subject, recruitmentPost.content, recruitmentPost.category.id,
					recruitmentPost.author.id.eq(siteUserId), recruitmentPost.author.name,
					recruitmentPost.author.profileImg,
					voter.countDistinct(), voter.siteUser.id.eq(siteUserId), recruitmentPost.createdAt,
					recruitmentPost.jobPosting.id, recruitmentPost.numOfApplicants, recruitmentPost.recruitmentStatus, recruitmentUser.countDistinct().intValue()))
			.from(recruitmentPost)
			.leftJoin(recruitmentPost.category)
			.leftJoin(recruitmentPost.author)
			.leftJoin(recruitmentPost.voterList, voter)
			.leftJoin(recruitmentUser)
			.on(recruitmentUser.post.postId.eq(postId))
			.groupBy(recruitmentPost.postId, recruitmentPost.subject, recruitmentPost.content, recruitmentPost.category.id, recruitmentPost.author.id,
				recruitmentPost.author.name, recruitmentPost.author.profileImg, voter.siteUser.id, recruitmentPost.createdAt,
				recruitmentPost.numOfApplicants, recruitmentPost.recruitmentStatus)
			.where(recruitmentPost.postId.eq(postId))
			.fetchOne();

		return Optional.ofNullable(postResponse);
	}
}
