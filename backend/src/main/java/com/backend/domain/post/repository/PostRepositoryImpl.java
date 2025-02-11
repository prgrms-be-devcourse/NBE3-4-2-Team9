package com.backend.domain.post.repository;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.util.PostSearchCondition;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

	private final PostJpaRepository postJpaRepository;
	private final PostQueryRepository postQueryRepository;

	@Override
	public Optional<Post> findById(Long postId) {
		return postJpaRepository.findById(postId);
	}

	@Override
	public Optional<Post> findByIdFetch(Long postId) {
		return postJpaRepository.findByIdFetch(postId);
	}

	@Override
	public Post save(Post post) {
		return postJpaRepository.save(post);
	}

	@Override
	public void deleteById(Long postId) {
		postJpaRepository.deleteById(postId);
	}

	@Override
	public Page<PostPageResponse> findAll(
		PostSearchCondition postSearchCondition,
		Pageable pageable) {

		return postQueryRepository.findAll(postSearchCondition, pageable);
	}
}
