package com.backend.domain.chat.dto.response;

import com.backend.domain.chat.entity.MessageType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record ChatResponse(
        Long id,
        Long userId,
        String username,
        String profileImg,
        MessageType type,
        ZonedDateTime createdAt,
        String content
) {
    // ✅ Jackson이 역직렬화를 수행할 수 있도록 @JsonCreator 추가
    @JsonCreator
    public ChatResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("userId") Long userId,
            @JsonProperty("username") String username,
            @JsonProperty("profileImg") String profileImg,
            @JsonProperty("type") MessageType type,
            @JsonProperty("createdAt") ZonedDateTime createdAt,
            @JsonProperty("content") String content
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.profileImg = profileImg;
        this.type = type;
        this.createdAt = createdAt;
        this.content = content;
    }

    // ✅ 정적 팩토리 메서드 추가 (record와 @Builder는 호환되지 않으므로 대체)
    public static ChatResponse of(Long id, Long userId, String username, String profileImg, MessageType type, ZonedDateTime createdAt, String content) {
        return new ChatResponse(id, userId, username, profileImg, type, createdAt, content);
    }
}
