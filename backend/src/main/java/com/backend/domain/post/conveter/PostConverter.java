package com.backend.domain.post.conveter;

import com.backend.domain.category.entity.Category;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.RecruitmentPostRequest;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.user.entity.SiteUser;

public class PostConverter {

	//게시글 저장할 때
	public static Post createPost(FreePostRequest freePostRequest, SiteUser siteUser, Category category) {
		return Post.builder()
			.author(siteUser)
			.subject(freePostRequest.getSubject())
			.content(freePostRequest.getContent())
			.category(category)
			.build();
	}

	// 모집 게시글 저장할 때
	public static Post createPost(RecruitmentPostRequest recruitmentPostRequest,
		Category category, SiteUser author, JobPosting jobPosting) {

		return Post.builder()
			.subject(recruitmentPostRequest.getSubject())
			.content(recruitmentPostRequest.getContent())
			.category(category)
			.author(author)
			.jobPosting(jobPosting)
			.numOfApplicants(recruitmentPostRequest.getNumOfApplicants())
			.recruitmentStatus(RecruitmentStatus.OPEN)
			.build();
	}

	public static PostResponse toPostResponse(Post post, boolean isAuthor) {
		return PostResponse.builder()
			.id(post.getPostId())
			.subject(post.getSubject())
			.content(post.getContent())
			.categoryId(post.getCategory().getId())
			.jobPostingId(post.getJobPosting().getId())
			.isAuthor(isAuthor)
			.authorName(post.getAuthor().getName())
			.authorImg(post.getAuthor().getProfileImg())
			.createdAt(post.getCreatedAt())
			.numOfApplicants(post.getNumOfApplicants())
			.recruitmentStatus(post.getRecruitmentStatus())
			.build();
	}

	public static PostCreateResponse toPostCreateResponse(Long postId, Long categoryId) {
		return PostCreateResponse.builder()
			.postId(postId) // 게시글 ID
			.categoryId(categoryId) // 카테고리 ID
			.build();
	}

}
