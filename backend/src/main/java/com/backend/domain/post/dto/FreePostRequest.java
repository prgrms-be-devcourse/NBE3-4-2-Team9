package com.backend.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FreePostRequest {

	@NotBlank(message = "제목을 입력해 주세요.")
	String subject;

	@NotBlank(message = "내용을 입력해 주세요.")
	String content;

}
