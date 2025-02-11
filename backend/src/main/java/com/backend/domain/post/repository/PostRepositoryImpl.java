package com.backend.domain.post.repository;

import com.backend.domain.post.entity.Post;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

	private final PostJpaRepository postJpaRepository;

	@Override
	public Optional<Post> findById(Long postId) {
		return Optional.empty();
	}

	@Override
	public Post save(Post post) {
		return postJpaRepository.save(post);
	}

	@Override
	public void deleteById(Long postId) {
		postJpaRepository.deleteById(postId);
	}
}
