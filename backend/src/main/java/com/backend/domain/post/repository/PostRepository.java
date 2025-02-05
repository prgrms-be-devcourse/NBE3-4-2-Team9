package com.backend.domain.post.repository;

import com.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostRepository extends JpaRepository<Post, Long> {

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


}
