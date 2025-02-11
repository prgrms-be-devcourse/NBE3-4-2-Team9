package com.backend.domain.recruitmentUser.dto.response;

import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import org.springframework.data.domain.Page;

public record RecruitmentPostResponse(
	RecruitmentUserStatus status,
	Page<PostResponse> postResponseDtoList
) {
	public static RecruitmentPostResponse from(RecruitmentUserStatus status,
		Page<Post> posts) {
		return new RecruitmentPostResponse(
			status,
			posts.map(post -> post.toDto(null)));
	}
}
