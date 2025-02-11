package com.backend.domain.comment.repository;

import com.backend.domain.comment.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.siteUser WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

}
