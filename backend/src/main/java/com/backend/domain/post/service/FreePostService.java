package com.backend.domain.post.service;

import com.backend.domain.category.domain.CategoryName;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.post.conveter.PostConverter;
import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.FreePost;
import com.backend.domain.post.repository.FreePostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreePostService {

	private final FreePostRepository freePostRepository;
	private final CategoryRepository categoryRepository;

	/**
	 * 게시글 조회하는 메서드 입니다.
	 *
	 * @param postId   조회할 게시글 아이디
	 * @param siteUser 로그인한 사용자
	 * @return {@link PostResponse}
	 * @throws GlobalException 게시글이 존재하지 않을 때 예외 발생
	 */

	@Transactional(readOnly = true)
	public PostResponse findById(Long postId, SiteUser siteUser) {
		return freePostRepository.findPostResponseById(postId, siteUser.getId())
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
	}

	/**
	 * 게시글 저장 메서드 입니다.
	 *
	 * @param freePostRequest 자유 게시글 관련 정보가 담긴 DTO
	 * @param siteUser        작성할 사용자
	 * @return {@link PostCreateResponse}
	 * @throws GlobalException 카테고리가 존재하지 않을 때 발생
	 */

	@Transactional
	public PostCreateResponse save(FreePostRequest freePostRequest, SiteUser siteUser) {
		log.info("free={}", freePostRequest);

		Category findCategory = categoryRepository.findByName(CategoryName.FREE.getValue())
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

		FreePost savePost = PostConverter.createPost(freePostRequest, siteUser, findCategory);

		FreePost savedPost = freePostRepository.save(savePost);

		return PostConverter.toPostCreateResponse(savedPost.getPostId(), findCategory.getId());
	}

	/**
	 * 게시글 수정 메서드 입니다.
	 *
	 * @param postId          수정할 게시글 ID
	 * @param freePostRequest 수정할 데이터를 담은 DTO
	 * @param siteUser        로그인한 사용자
	 * @return {@link PostResponse}
	 * @throws GlobalException
	 */

	@Transactional
	public PostResponse update(Long postId, FreePostRequest freePostRequest, SiteUser siteUser) {
		FreePost target = freePostRepository.findById(postId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

		if (!target.getAuthor().getId().equals(siteUser.getId())) {
			throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
		}

		target.setSubject(freePostRequest.getSubject());
		target.setContent(freePostRequest.getContent());

		FreePost updatedPost = freePostRepository.save(target);

		return PostConverter.toPostResponse(updatedPost, true);
	}

	/**
	 * 게시글 삭제 메서드 입니다.
	 *
	 * @param postId   삭제할 게시글 ID
	 * @param siteUser 로그인한 사용자
	 */

	@Transactional
	public void delete(Long postId, SiteUser siteUser) {
		FreePost findPost = freePostRepository.findById(postId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

		if (!findPost.getAuthor().getId().equals(siteUser.getId())) {
			throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
		}
		freePostRepository.deleteById(findPost.getPostId());
	}
}
