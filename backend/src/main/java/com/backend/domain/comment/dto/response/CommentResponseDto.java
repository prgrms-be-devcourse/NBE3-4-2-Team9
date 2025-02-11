package com.backend.domain.comment.dto.response;

import com.backend.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    /**
     * { "id": 1, "content": "작성한 댓글 내용", "createdAt": "2025-02-07T14:27:28.271879+09:00",
     * "modifiedAt": "2025-02-07T14:27:28.271879+09:00", "isAuthor" : "작성자가 맞는지" }
     */

    private Long id;
    private String content;
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;
    private boolean isAuthor;

    public static CommentResponseDto convertEntity(Comment comment, boolean isAuthor) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .isAuthor(isAuthor)
            .build();
    }

}
