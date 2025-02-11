package com.backend.domain.comment.service;

import com.backend.domain.comment.dto.request.CommentRequestDto;
import com.backend.domain.comment.dto.response.CommentCreateResponseDto;
import com.backend.domain.comment.dto.response.CommentResponseDto;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponseDto createComment(CommentRequestDto dto, Long postId,
        CustomUserDetails user) {

        // 게시글정보가 db에 있는지에 대한 검증
        Post findPost = postRepository.findById(postId).orElseThrow(
            () -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND)
        );

        Comment comment = Comment.builder()
            .content(dto.getContent())
            .post(findPost)
            .siteUser(user.getSiteUser())
            .build();

        Comment saveComment = commentRepository.save(comment);

        return CommentCreateResponseDto.convertEntity(saveComment);
    }

    @Transactional
    public CommentResponseDto modifyComment(Long postId, Long commentId, CommentRequestDto dto, CustomUserDetails user) {

        // 게시글정보가 db에 있는지에 대한 검증
        postRepository.findById(postId).orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        // 댓글정보가 db에 있는지에 대한 검증
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new GlobalException(GlobalErrorCode.COMMENT_NOT_FOUND));

        // 로그인한 사용자와 댓글 작성자가 일치하는지 검증
        if (!user.getSiteUser().getId().equals(comment.getSiteUser().getId())) {
            throw new GlobalException(GlobalErrorCode.COMMENT_NOT_AUTHOR);
        }

        comment.ChangeContent(dto.getContent());
        Comment modifiedComment = commentRepository.save(comment);

        return CommentResponseDto.convertEntity(modifiedComment, true);
    }

    @Transactional
    public void deleteComment(Long commentId, SiteUser user) {

        Comment findComment = commentRepository.findById(commentId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.COMMENT_NOT_FOUND)
        );

        if (!findComment.getSiteUser().getId().equals(user.getId())) {
            throw new GlobalException(GlobalErrorCode.COMMENT_NOT_AUTHOR);
        }

        commentRepository.deleteById(commentId);
    }

}
