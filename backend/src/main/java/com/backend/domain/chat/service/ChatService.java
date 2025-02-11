package com.backend.domain.chat.service;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.dto.response.ChatsInPost;
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
    public ChatResponse save(ChatRequest chatRequest, Long postId, Map<String, Object> header) {
        Chat chat = chatMapper.toChat(chatRequest, postId);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    /**
     * 채팅 조회
     */

    public ChatsInPost getByPostId(Long postId, Pageable pageable) {
        Page<ChatResponse> result = chatRepository.findByPostId(postId, pageable);
        return new ChatsInPost(result);
    }

    public ChatResponses getAllByPostId(Long postId) {
        List<ChatResponse> result = chatRepository.findAllByPostId(postId);
        return new ChatResponses(result);
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
