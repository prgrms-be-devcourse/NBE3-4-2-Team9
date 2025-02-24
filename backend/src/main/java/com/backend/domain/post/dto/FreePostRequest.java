package com.backend.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class FreePostRequest {

	@NotBlank(message = "제목을 입력해 주세요.")
	private String subject;
	@NotBlank(message = "내용을 입력해 주세요.")
	private String content;
	@NotNull(message = "카테고리를 선택해 주세요.")
	private Long categoryId;
}
