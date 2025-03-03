/*
package com.backend.domain.post.repository;

import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostRepository2 extends JpaRepository<Post, Long> {

    // 카테고리와 제목, 내용에서 검색어 포함 여부를 처리하는 쿼리
    @Query("SELECT p FROM Post p WHERE " +
            "(:categoryId IS NULL OR p.categoryId.id = :categoryId) AND " +
            "(:keyword IS NULL OR p.subject LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByCategoryAndKeyword(@Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 제목 또는 내용에서 검색어만 검색하는 쿼리
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR p.subject LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // 전체 게시글 조건 없이 조회
    @Query("SELECT p FROM Post p WHERE (:categoryId IS NULL OR p.categoryId.id = :categoryId)")
    Page<Post> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // 제목으로 조회
    Optional<Post> findBySubject(String subject);

    // 모집 마감해야 할 게시글 조회
    @Query("SELECT p from Post p where p.recruitmentClosingDate "
            + "<= :now and p.recruitmentStatus = :status")
    List<Post> findExpiredRecruitmentPosts(@Param("now") ZonedDateTime now);

    List<Post> findByRecruitmentStatus(RecruitmentStatus recruitmentStatus);
}

*/
