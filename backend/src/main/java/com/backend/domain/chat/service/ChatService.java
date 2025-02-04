package com.backend.domain.chat.service;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.dto.response.ChatsInBoard;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.mapper.ChatMapper;
import com.backend.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    /**
     * 채팅 저장
     */
    @Transactional
    public ChatResponse save(ChatRequest chatRequest, Long boardId, Map<String, Object> header) {
        Chat chat = chatMapper.toChat(chatRequest, boardId);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    /**
     * 채팅 조회
     */

    public ChatsInBoard getByBoardId(Long boradId, Pageable pageable) {
        Page<ChatResponse> result = chatRepository.findByBoardId(boradId, pageable);
        return new ChatsInBoard(result);
    }

    public List<ChatResponse> getAllByBoardId(Long boardId) {
        return chatRepository.findAllByBoardId(boardId);
    }

    private ChatResponse toChatResponse(Chat chat, Map<String, Object> header) {
        String username = getValueFromHeader(header, "username");
        String profileImg = getValueFromHeader(header, "profileImg");

        return chatMapper.toChatResponse(chat, username, profileImg);
    }

    private String getValueFromHeader(Map<String, Object> header, String key) {
        return (String) header.get(key);
    }
}
