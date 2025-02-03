package com.backend.domain.board.repository;

import com.backend.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리와 제목, 내용에서 검색어 포함 여부를 처리하는 쿼리
    // category 참조 때문에 추후에 작업 예정
//    @Query("SELECT p FROM Post p WHERE " +
//            "(:categoryId IS NULL OR p.categoryId.id = :categoryId) AND " +
//            "(:keyword IS NULL OR p.subject LIKE %:keyword% OR p.content LIKE %:keyword%)")
//    Page<Post> findByCategoryAndKeyword(@Param("categoryId") Long categoryId,
//            @Param("keyword") String keyword,
//            Pageable pageable);

    // 제목 또는 내용에서 검색어만 검색하는 쿼리
    @Query("SELECT b FROM Board b WHERE " +
            "(:keyword IS NULL OR b.subject LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
