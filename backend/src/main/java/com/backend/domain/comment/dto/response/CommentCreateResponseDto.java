package com.backend.domain.comment.dto.response;

import com.backend.domain.comment.entity.Comment;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateResponseDto {

    /**
     * {
     *     "id": 1,
     *     "content": "작성한 댓글 내용",
     *     "createdAt": "2025-02-07T14:27:28.271879+09:00",
     *     "modifiedAt": "2025-02-07T14:27:28.271879+09:00"
     * }
     */

    private Long id;

    private String content;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;


    public static CommentCreateResponseDto convertEntity(Comment comment) {
        return CommentCreateResponseDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build();
    }

}
