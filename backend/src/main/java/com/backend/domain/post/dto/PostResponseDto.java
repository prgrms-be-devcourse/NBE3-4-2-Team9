package com.backend.domain.post.dto;

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
    private ZonedDateTime recruitmentClosingDate;
    private Integer numOfApplicants;
    private String recruitmentStatus; // Enum -> String

}
