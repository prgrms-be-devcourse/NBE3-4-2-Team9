package com.backend.domain.post.dto;

import com.backend.domain.post.entity.RecruitmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostResponse {

	private Long id;
	private String subject;
	private String content;
	private Long categoryId;
	private Long jobPostingId;
	private boolean isAuthor;
	private String authorName;
	private String authorImg;
	private ZonedDateTime createdAt;

	// 모집 게시판 전용 필드
	@JsonInclude(value = Include.NON_NULL)
	private Integer numOfApplicants;

	@JsonInclude(value = Include.NON_NULL)
	private RecruitmentStatus recruitmentStatus; // Enum -> String

}
