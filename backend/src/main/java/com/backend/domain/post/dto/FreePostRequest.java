package com.backend.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FreePostRequest {

	@NotBlank(message = "제목을 입력해 주세요.")
	String subject;

	@NotBlank(message = "내용을 입력해 주세요.")
	String content;

	@NotNull(message = "카테고리를 선택해 주세요.")
	Long categoryId;
}
