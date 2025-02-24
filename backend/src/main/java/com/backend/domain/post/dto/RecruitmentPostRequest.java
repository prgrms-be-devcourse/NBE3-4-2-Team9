package com.backend.domain.post.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class RecruitmentPostRequest extends FreePostRequest {

	@NotNull(message = "채용 공고를 선택해 주세요.")
	private Long jobPostingId; // 모집 게시판 아닐 경우 null

	@Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다.")
	private Integer numOfApplicants;

	@NotNull(message = "모집 마감일을 입력해 주세요.")
	@Future(message = "모집 종료일은 미래 날짜여야 합니다.")
	private ZonedDateTime RecruitmentClosingDate;

}
