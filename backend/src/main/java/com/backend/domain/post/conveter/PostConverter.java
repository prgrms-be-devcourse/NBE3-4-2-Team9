package com.backend.domain.post.conveter;

import com.backend.domain.category.entity.Category;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.RecruitmentPostRequest;
import com.backend.domain.post.entity.FreePost;
import com.backend.domain.post.entity.RecruitmentPost;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.user.entity.SiteUser;

public class PostConverter {

    // 자유 게시글 저장할 때
    public static FreePost createPost(FreePostRequest freePostRequest, SiteUser siteUser,
            Category category) {
        return FreePost.builder()
                .author(siteUser)
                .subject(freePostRequest.getSubject())
                .content(freePostRequest.getContent())
                .category(category)
                .build();
    }

    // 모집 게시글 저장할 때
    public static RecruitmentPost createPost(RecruitmentPostRequest recruitmentPostRequest,
            Category category, SiteUser author, JobPosting jobPosting) {

        return RecruitmentPost.builder()
                .subject(recruitmentPostRequest.getSubject())
                .content(recruitmentPostRequest.getContent())
                .category(category)
                .author(author)
                .jobPosting(jobPosting)
                .numOfApplicants(recruitmentPostRequest.getNumOfApplicants())
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .build();
    }


    public static PostResponse toPostResponse(Post post, boolean isAuthor) {
        return PostResponse.builder()
                .id(post.getPostId())
                .subject(post.getSubject()) // 게시글 제목
                .content(post.getContent()) // 게시글 내용
                .categoryId(post.getCategory().getId()) // 카테고리 ID
                .jobPostingId(
                        post.getJobPosting() != null ? post.getJobPosting().getId() : null) // 채용 ID
                .isAuthor(isAuthor) // 현재 사용자가 작성자인지 여부
                .authorName(post.getAuthor().getName()) // 작성자 이름
                .authorImg(post.getAuthor().getProfileImg()) // 작성자 프로필 이미지
                .createdAt(post.getCreatedAt()) // 생성일
                .numOfApplicants(
                        post.getNumOfApplicants() != null ? post.getNumOfApplicants()
                                : null) // 모집 인원
                .recruitmentStatus(
                        post.getRecruitmentStatus() != null ? post.getRecruitmentStatus()
                                : null) // 모집 상태
                .build();
    }

    public static PostResponse toPostResponse(Post post, boolean isAuthor,
            int currentAcceptedCount) {
        return PostResponse.builder()
                .id(post.getPostId())
                .subject(post.getSubject()) // 게시글 제목
                .content(post.getContent()) // 게시글 내용
                .categoryId(post.getCategory().getId()) // 카테고리 ID
                .jobPostingId(
                        post.getJobPosting() != null ? post.getJobPosting().getId() : null) // 채용 ID
                .isAuthor(isAuthor) // 현재 사용자가 작성자인지 여부
                .authorName(post.getAuthor().getName()) // 작성자 이름
                .authorImg(post.getAuthor().getProfileImg()) // 작성자 프로필 이미지
                .createdAt(post.getCreatedAt()) // 생성일
                .numOfApplicants(
                        post.getNumOfApplicants() != null ? post.getNumOfApplicants()
                                : null) // 모집 인원
                .recruitmentStatus(
                        post.getRecruitmentStatus() != null ? post.getRecruitmentStatus()
                                : null) // 모집 상태
                .currentAcceptedCount(currentAcceptedCount)
                .build();
    }

    public static PostCreateResponse toPostCreateResponse(Long postId, Long categoryId) {
        return PostCreateResponse.builder()
                .postId(postId) // 게시글 ID
                .categoryId(categoryId) // 카테고리 ID
                .build();
    }
}
