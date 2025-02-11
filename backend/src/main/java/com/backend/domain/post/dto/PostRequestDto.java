package com.backend.domain.post.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String subject;
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;
    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;
    @Builder.Default // 기본값
    private Long jobPostingId = null; // 모집 게시판 아닐 경우 null
    // 모집 게시판 전용 필드 추가
    @Future(message = "모집 종료일은 미래 날짜여야 합니다.") //
    private ZonedDateTime RecruitmentClosingDate;
    @Min(value = 1, message = "모집 인원은 최소 1명 이상이오야 합니다.")
    private Integer numOfApplicants;

}

