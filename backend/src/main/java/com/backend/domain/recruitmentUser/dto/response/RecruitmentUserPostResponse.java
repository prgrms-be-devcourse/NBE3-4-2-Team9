package com.backend.domain.recruitmentUser.dto.response;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import org.springframework.data.domain.Page;

public record RecruitmentUserPostResponse(
	RecruitmentUserStatus status,
	Page<PostPageResponse> postPageResponses
) {

}