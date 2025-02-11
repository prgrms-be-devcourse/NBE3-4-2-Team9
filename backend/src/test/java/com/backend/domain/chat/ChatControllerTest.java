package com.backend.domain.chat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.chat.repository.ChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class ChatControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long postId = 1L; // init.sql에서 사용된 postId 값

    @Test
    @DisplayName("채팅 목록 전체 조회 성공")
    void testGetChattingList() throws Exception {
        mockMvc.perform(get("/api/v1/chat/list/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.chats").isArray())
                .andExpect(jsonPath("$.data.chats.length()").value(4)) // 총 4개 데이터 존재 (init.sql 기준)
                .andDo(print());
    }

    @Test
    @DisplayName("채팅 목록 페이징 조회 성공")
    void testGetChattingListWithPaging() throws Exception {
        mockMvc.perform(get("/api/v1/chat/page/{postId}", postId)
                        .param("size", "10")  // 페이지 크기 2
                        .param("page", "0")  // 첫 번째 페이지 요청
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())  // `content` 필드 확인
                .andExpect(jsonPath("$.data.content.length()").value(4)) // 페이징 크기 적용
                .andExpect(jsonPath("$.data.pageNumber").value(0))  // 현재 페이지 번호 확인
                .andExpect(jsonPath("$.data.pageSize").value(10))  // 페이지 크기 확인
                .andExpect(jsonPath("$.data.totalElements").isNumber())  // 총 요소 개수 확인
                .andExpect(jsonPath("$.data.totalPages").isNumber())  // 총 페이지 개수 확인
                .andExpect(jsonPath("$.data.last").isBoolean())  // 마지막 페이지 여부 확인
                .andDo(print());
    }



    @Test
    @DisplayName("채팅 목록 조회 실패 - 존재하지 않는 postId")
    void testGetChattingListEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/chat/list/{postId}", 999L) // 존재하지 않는 게시글 ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.chats.length()").value(0)) // 데이터 없음
                .andDo(print());
    }
}
