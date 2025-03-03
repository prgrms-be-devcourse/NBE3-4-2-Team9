package com.backend.domain.post.service;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.repository.post.PostRepository;
import com.backend.domain.post.util.PostSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;

	/**
	 * 게시글 전체 동적 조회 메서드 입니다.
	 *
	 * @param postSearchCondition 검색 조건
	 * @return {@link Page<PostPageResponse>}
	 */
	@Transactional(readOnly = true)
	public Page<PostPageResponse> findAll(PostSearchCondition postSearchCondition) {
		int pageNum = postSearchCondition.pageNum() == null ?
			0 : postSearchCondition.pageNum();

		int pageSize = postSearchCondition.pageSize() == null ?
			10 : postSearchCondition.pageSize();

		Pageable pageable = PageRequest.of(pageNum, pageSize);

		Page<PostPageResponse> postPageResponsePage =
			postRepository.findAll(postSearchCondition, pageable);

		return postPageResponsePage;
	}

}
