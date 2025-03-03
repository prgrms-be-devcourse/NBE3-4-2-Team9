package com.backend.domain.post.repository.post;

import static com.backend.domain.comment.entity.QComment.comment;
import static com.backend.domain.post.entity.QPost.post;
import static com.backend.domain.recruitmentUser.entity.QRecruitmentUser.recruitmentUser;
import static com.backend.domain.voter.entity.QVoter.voter;

import com.backend.domain.category.domain.CategoryName;
import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.QPostPageResponse;
import com.backend.domain.post.dto.QPostResponse;
import com.backend.domain.post.util.PostSearchCondition;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 게시글 조회 리포지토리 입니다.
 *
 * @author Kim Dong O
 */
@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<PostPageResponse> findAll(PostSearchCondition postSearchCondition,
		Pageable pageable) {
		List<PostPageResponse> content = queryFactory
			.selectDistinct(new QPostPageResponse(
				post.postId, post.subject, post.category.name,
				post.author.name, post.author.profileImg,
				comment.countDistinct(), voter.countDistinct(), post.createdAt)
			)
			.from(post)
			.leftJoin(post.category)
			.leftJoin(post.author)
			.leftJoin(post.commentList, comment)
			.leftJoin(post.voterList, voter)
			.groupBy(post.postId, post.subject, post.category.name,
				post.author.name, post.author.profileImg, post.createdAt)
			.where(
				getCategoryIdEq(postSearchCondition.categoryId()),
				getSubjectContains(postSearchCondition.kw())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(post.count())
			.from(post)
			.where(
				getCategoryIdEq(postSearchCondition.categoryId()),
				getSubjectContains(postSearchCondition.kw())
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public Page<PostPageResponse> findRecruitmentAll(Pageable pageable, Long userId, RecruitmentUserStatus status) {
		List<PostPageResponse> content = queryFactory
			.selectDistinct(new QPostPageResponse(
				post.postId, post.subject, post.category.name,
				post.author.name, post.author.profileImg,
				comment.countDistinct(), voter.countDistinct(), post.createdAt)
			)
			.from(post)
			.leftJoin(post.category)
			.leftJoin(post.author)
			.leftJoin(post.commentList, comment)
			.leftJoin(post.voterList, voter)
			.leftJoin(recruitmentUser)
			.on(recruitmentUser.siteUser.id.eq(userId).and(recruitmentUser.status.eq(status)))
			.groupBy(post.postId, post.subject, post.category.name,
				post.author.name, post.author.profileImg, post.createdAt)
			.where(
				post.category.name.eq(CategoryName.RECRUITMENT.getValue())
					.and(recruitmentUser.siteUser.id.eq(userId))
					.and(recruitmentUser.status.eq(status)))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(post.count())
			.from(post)
			.leftJoin(recruitmentUser)
			.on(recruitmentUser.siteUser.id.eq(userId).and(recruitmentUser.status.eq(status)));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public Optional<PostResponse> findPostResponseById(Long postId, Long siteUserId) {
		PostResponse postResponse = queryFactory.selectDistinct(
				new QPostResponse(post.postId, post.subject, post.content, post.category.id,
					post.author.id.eq(siteUserId), post.author.name,
					post.author.profileImg,
					voter.countDistinct(), voter.siteUser.id.eq(siteUserId), post.createdAt))
			.from(post)
			.leftJoin(post.category)
			.leftJoin(post.author)
			.leftJoin(post.voterList, voter)
			.leftJoin(recruitmentUser)
			.on(recruitmentUser.post.postId.eq(postId))
			.groupBy(post.postId, post.subject, post.content, post.category.id, post.author.id,
				post.author.name, post.author.profileImg, voter.siteUser.id, post.createdAt)
			.where(post.postId.eq(postId))
			.fetchOne();

		return Optional.ofNullable(postResponse);
	}

	/**
	 * 정렬할 필드와 정렬 방식을 OrderSpecifier로 반환합니다.
	 *
	 * @param postSearchCondition
	 * @return {@link OrderSpecifier <?>}
	 */
	private OrderSpecifier<?> getOrderBy(PostSearchCondition postSearchCondition) {
		// 기본 정렬 방식 설정
		Order queryOrder =
			Order.ASC.toString().equalsIgnoreCase(postSearchCondition.order()) ?
				Order.ASC : Order.DESC;

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"commentCount", post.commentList.size(),
			"voter", post.voterList.size(),
			"createdAt", post.createdAt
		);

		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(postSearchCondition.sort()) && fieldMap.containsKey(
				postSearchCondition.sort()) ?
				fieldMap.get(postSearchCondition.sort()) : post.createdAt;

		return new OrderSpecifier<>(queryOrder, sortField);
	}

	/**
	 * 키워드 조건식을 반환합니다.
	 *
	 * @param kw
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getSubjectContains(String kw) {
		return StringUtils.hasText(kw) ? post.subject.contains(kw) : null;
	}

	/**
	 * 카테고리 조건식을 반환합니다.
	 *
	 * @param categoryId
	 * @return {@link BooleanExpression}
	 */
	private BooleanExpression getCategoryIdEq(Long categoryId) {
		return categoryId != null ? post.category.id.eq(categoryId) : null;
	}

}
