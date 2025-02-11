package com.backend.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostResponseDto {

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
	private ZonedDateTime recruitmentClosingDate;

	@JsonInclude(value = Include.NON_NULL)
	private Integer numOfApplicants;

	@JsonInclude(value = Include.NON_NULL)
	private String recruitmentStatus; // Enum -> String

}
