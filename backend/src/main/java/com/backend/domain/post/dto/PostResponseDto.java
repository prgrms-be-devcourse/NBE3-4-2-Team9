package com.backend.domain.post.dto;

import com.backend.domain.post.entity.Post;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String subject;
    private String content;
    private Long categoryId;
    private ZonedDateTime createdAt;

    // Entity -> DTO 변환(Builder 활용)
    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .content(post.getContent())
                // TODO: category, jobposting 미구현, 구현 이후 다시 작업
                .categoryId(post.getCategoryId().getId())
//                .jobId(post.getJobId().getId())
                .createdAt(post.getCreatedAt())
                .build();

    }
}
