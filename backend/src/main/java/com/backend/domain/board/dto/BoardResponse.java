package com.backend.domain.board.dto;

import com.backend.domain.board.entity.Board;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String subject;
    private String content;
    private Long categoryId;
    private ZonedDateTime createdAt;

    // Entity -> DTO 변환(Builder 활용)
    public static BoardResponse fromEntity(Board board){
        return BoardResponse.builder()
                .id(board.getBoardId())
                .subject(board.getSubject())
                .content(board.getContent())
            // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//                .categoryId(post.getCategoryId().getId())
//                .jobId(post.getJobId().getId())
                .createdAt(board.getCreatedAt())
                .build();

    }
}
