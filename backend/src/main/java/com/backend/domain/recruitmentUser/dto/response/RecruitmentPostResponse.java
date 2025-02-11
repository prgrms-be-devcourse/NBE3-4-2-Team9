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
			posts.map(post -> null));
		//TODO 추후 페이징 응답 객체 생성 후 수정 예정
	}
}
