package com.backend.domain.chat.repository;

import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // DTO로 반환
    @Query("""
            	SELECT 
            	new com.backend.domain.chat.dto.response.ChatResponse
            	(c.id, u.id, u.name, u.profileImg, c.type, c.createdAt, c.content) 
            	FROM Chat c 
            	JOIN SiteUser u ON u.id = c.userId 
            	WHERE c.boardId = :boardId
            """)
    Page<ChatResponse> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("""
            	SELECT 
            	new com.backend.domain.chat.dto.response.ChatResponse
            	(c.id, u.id, u.name, u.profileImg, c.type, c.createdAt, c.content) 
            	FROM Chat c 
            	JOIN SiteUser u ON u.id = c.userId 
            	WHERE c.boardId = :boardId
            """)
    List<ChatResponse> findAllByBoardId(@Param("boardId") Long boardId);
}