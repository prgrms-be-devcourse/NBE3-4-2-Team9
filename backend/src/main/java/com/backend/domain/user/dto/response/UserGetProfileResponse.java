package com.backend.domain.user.dto.response;

import com.backend.domain.comment.entity.Comment;
import com.backend.domain.jobskill.dto.JobSkillResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.user.entity.SiteUser;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserGetProfileResponse {

    private final String name;

    private final String email;

    private final String introduction;

    private final String job;

    private final List<JobSkillResponse> jobSkills;

    private final String profileImg;

	private final List<UserPostResponse> posts;

	private final List<UserCommentResponse> comments;

	public UserGetProfileResponse(SiteUser siteUser) {
		this.name = siteUser.getName();
		this.email = siteUser.getEmail();
		this.introduction = siteUser.getIntroduction();
		this.job = siteUser.getJob();
		this.jobSkills = siteUser.getJobSkills() != null ? siteUser.getJobSkills().stream()
			.map((j) -> JobSkillResponse.builder()
                .code(j.getCode())
                .name(j.getName())
                .build())
			.collect(Collectors.toList()) : null;
		this.profileImg = siteUser.getProfileImg();
		this.posts = siteUser.getPosts().stream()
				.map(UserPostResponse::new)
				.collect(Collectors.toList());
		this.comments = siteUser.getComments().stream()
				.map(UserCommentResponse::new)
				.collect(Collectors.toList());
	}

}

@Getter
@AllArgsConstructor
class UserPostResponse {
	private Long postId;
	private String subject;
	private ZonedDateTime createdAt;

	public UserPostResponse(Post post) {
		this.postId = post.getPostId();
		this.subject = post.getSubject();
		this.createdAt = post.getCreatedAt();
	}
}

@Getter
@AllArgsConstructor
class UserCommentResponse {
	private Long commentId;
	private String content;
	private Long postId;
	private ZonedDateTime createdAt;

	public UserCommentResponse(Comment comment) {
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.postId = comment.getPost().getPostId();
		this.createdAt = comment.getCreatedAt();
	}
}
