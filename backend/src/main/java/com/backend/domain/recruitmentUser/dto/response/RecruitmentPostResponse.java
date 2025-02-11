package com.backend.domain.recruitmentUser.dto.response;

import org.springframework.data.domain.Page;

import com.backend.domain.post.dto.PostResponseDto;
import com.backend.domain.post.entity.Post;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;

public record RecruitmentPostResponse(
	RecruitmentUserStatus status,
	Page<PostResponseDto> postResponseDtoList
) {
	public static RecruitmentPostResponse from(RecruitmentUserStatus status,
		Page<Post> posts) {
		return new RecruitmentPostResponse(
			status,
			posts.map(PostResponseDto::fromEntity));
	}
}
