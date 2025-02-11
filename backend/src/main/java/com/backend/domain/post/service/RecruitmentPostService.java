package com.backend.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.category.domain.CategoryName;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.post.conveter.PostConverter;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.RecruitmentPostRequest;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

/**
 * RecruitmentPostService 모집 게시글을 담당하는 서비스 클래스입니다.
 * 모집 게시글 생성
 * 모집 게시글 수정
 * 모집 게시글 삭제
 *
 * @author Hyeonsuk
 */
@Service
@RequiredArgsConstructor
public class RecruitmentPostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final JobPostingRepository jobPostingRepository;

	// ==============================
	//  1. 비즈니스 로직
	// ==============================

	/**
	 * 모집 게시글을 생성합니다.
	 *
	 * @param recruitmentPostRequest 모집 게시글 관련 정보가 담긴 DTO
	 * @param siteUser               현재 게시글을 작성하는 사용자
	 * @return 생성된 게시글의 ID와 카테고리 ID를 포함한 응답 DTO
	 * @throws GlobalException 카테고리 또는 채용 공고가 존재하지 않을 경우 예외 발생
	 */
	@Transactional
	public PostCreateResponse save(RecruitmentPostRequest recruitmentPostRequest, SiteUser siteUser) {

		Category category = categoryRepository.findByName(CategoryName.RECRUITMENT.getValue())
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

		JobPosting jobPosting = jobPostingRepository.findById(recruitmentPostRequest.getJobPostingId())
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.JOB_POSTING_NOT_FOUND));

		Post post = PostConverter.createPost(
			recruitmentPostRequest,
			category,
			siteUser,
			jobPosting
		);

		Post savePost = postRepository.save(post);

		return PostConverter.toPostCreateResponse(savePost.getPostId(), savePost.getCategory().getId());
	}

	/**
	 * 모집 게시글을 수정합니다.
	 *
	 * @param postId                  수정할 게시글의 ID
	 * @param recruitmentPostRequest   수정할 정보가 담긴 DTO
	 * @param siteUser                현재 게시글을 수정하는 사용자
	 * @return 수정된 게시글 정보를 담은 DTO
	 * @throws GlobalException 게시글이 존재하지 않거나, 작성자가 아닐 경우 예외 발생
	 */
	@Transactional
	public PostResponse update(Long postId, RecruitmentPostRequest recruitmentPostRequest, SiteUser siteUser) {

		Post findPost = getPost(postId);

		validateAuthor(siteUser, findPost);

		findPost.updatePost(
			recruitmentPostRequest.getSubject(),
			recruitmentPostRequest.getContent(),
			recruitmentPostRequest.getNumOfApplicants()
		);

		return PostConverter.toPostResponse(findPost, true);
	}

	/**
	 * 모집 게시글을 삭제합니다.
	 *
	 * @param postId    삭제할 게시글의 ID
	 * @param siteUser  현재 게시글을 삭제하는 사용자
	 * @throws GlobalException 게시글이 존재하지 않거나, 작성자가 아닐 경우 예외 발생
	 */
	@Transactional
	public void delete(Long postId, SiteUser siteUser) {

		Post findPost = getPost(postId);

		validateAuthor(siteUser, findPost);

		postRepository.deleteById(postId);
	}

	// ==============================
	//  2. 검증 메서드
	// ==============================

	/**
	 * 게시글의 작성자를 검증합니다.
	 *
	 * @param siteUser 현재 사용자
	 * @param findPost 조회된 게시글
	 * @throws GlobalException 사용자가 게시글의 작성자가 아닐 경우 예외 발생
	 */
	private static void validateAuthor(SiteUser siteUser, Post findPost) {
		if (findPost.getAuthor() != siteUser) {
			throw new GlobalException(GlobalErrorCode.NOT_AUTHOR); // 작성자가 아닐 경우 예외 발생
		}
	}

	// ==============================
	//  3. DB 조회 메서드
	// ==============================

	/**
	 * 게시글을 조회합니다.
	 *
	 * @param postId 모집 게시글 ID
	 * @return 조회된 게시글 엔티티
	 * @throws GlobalException 게시글이 존재하지 않을 경우 예외 발생
	 */
	private Post getPost(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
	}

}
