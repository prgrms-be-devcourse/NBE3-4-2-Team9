package com.backend.domain.post.repository.post;

import com.backend.domain.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

	@Query("select p from Post p left join fetch p.author left join fetch p.jobPosting where p.postId = :postId")
	Optional<Post> findByIdFetch(@Param("postId") Long postId);
}
