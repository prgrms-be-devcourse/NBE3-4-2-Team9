package com.backend.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.chat.util.ChatObjectProvider;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    private SiteUser testUser;
    private SiteUser otherUser;
    private Long boardId;

    @BeforeEach
    void setUp() {
        // 테스트 유저 저장
        testUser = userRepository.save(SiteUser.builder()
                .name("testUser")
                .email("test@test.com")
                .password("test")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        otherUser = userRepository.save(SiteUser.builder()
                .name("otherUser")
                .email("other@test.com")
                .password("password")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        boardId = 1L;

        // ChatObjectProvider를 사용하여 두 개의 사용자 채팅 데이터 저장
        List<Chat> chatList1 = ChatObjectProvider.getChatList(boardId, testUser.getId());
        List<Chat> chatList2 = ChatObjectProvider.getChatList(boardId, otherUser.getId());

        chatRepository.saveAll(chatList1);
        chatRepository.saveAll(chatList2);
    }

    @Test
    @DisplayName("채팅 목록 조회 성공 - 페이징 적용")
    void testFindByBoardIdWithPaging() {
        // Given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdAt")));

        // When
        Page<ChatResponse> result = chatRepository.findByBoardId(boardId, pageable);

        // Then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(20); // 두 명의 사용자이므로 총 20개
    }

    @Test
    @DisplayName("채팅 목록 전체 조회 성공")
    void testFindAllByBoardId() {
        // When
        List<ChatResponse> chatList = chatRepository.findAllByBoardId(boardId);

        // Then
        assertThat(chatList).hasSize(20);
        assertThat(chatList.getFirst().userId()).isEqualTo(testUser.getId());
        assertThat(chatList.getLast().userId()).isEqualTo(otherUser.getId());
    }

    @Test
    @DisplayName("채팅 목록 조회 실패 - 존재하지 않는 boardId")
    void testFindByBoardIdEmpty() {
        // When
        List<ChatResponse> chatList = chatRepository.findAllByBoardId(999L);

        // Then
        assertThat(chatList).isEmpty();
    }
}