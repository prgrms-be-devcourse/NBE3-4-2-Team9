package com.backend.domain.post.repository.post;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.util.PostSearchCondition;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

	/**
	 * @param postId 게시글 Id
	 * @return {@link Optional<Post>}
	 * @implSpec 게시글 ID로 단건 조회 메서드 입니다.
	 */
	Optional<Post> findById(Long postId);

	/**
	 * @param postId 게시글 Id
	 * @return {@link Optional<Post>}
	 * @implSpec 게시글 ID로 단건 조회 메서드 입니다. (fetch join 사용)
	 */
	Optional<Post> findByIdFetch(Long postId);

	/**
	 * @param post 게시글 엔티티
	 * @return {@link Post}
	 * @implSpec 게시글 저장 메서드 입니다.
	 */
	Post save(Post post);

	/**
	 * @param postId 게시글 Id
	 * @implSpec 게시글 삭제 메서드 입니다.
	 */
	void deleteById(Long postId);

	/**
	 *
	 * @param postSearchCondition 검색 조건 객체 {@link PostSearchCondition}
	 * @param pageable pageable
	 * @implSpec 게시글 전체 동적 조회 메서드 입니다.
	 * @return {@link Page<PostPageResponse>}
	 */
	Page<PostPageResponse> findAll(PostSearchCondition postSearchCondition, Pageable pageable);

	/**
	 * @param userId userId
	 * @param status status
	 * @param pageable pageable
	 * @implSpec 게시글 전체 동적 조회 메서드 입니다.
	 * @return {@link Page<PostPageResponse>}
	 */
	Page<PostPageResponse> findRecruitmentAll(Long userId, RecruitmentUserStatus status, Pageable pageable);

	/**
	 *
	 * @param postId 조회할 게시글 ID
	 * @param siteUserId 로그인한 사용자 ID
	 * @implSpec 게시글 상세 조회 메서드 입니다.
	 * @return {@link Optional<PostResponse>}
	 */
	Optional<PostResponse> findPostResponseById(Long postId, Long siteUserId);

}
