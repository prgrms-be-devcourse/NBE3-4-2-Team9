package com.backend.domain.post.repository;

import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.FreePost;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreePostRepository extends JpaRepository<FreePost, Long> {

    @Query("SELECT new com.backend.domain.post.dto.PostResponse(p) " +
            "FROM FreePost p WHERE p.postId = :postId AND p.author.id = :userId")
    Optional<PostResponse> findPostResponseById(@Param("postId") Long postId, @Param("userId") Long userId);

}
