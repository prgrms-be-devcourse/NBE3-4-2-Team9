package com.backend.domain.chat.dto.response;

import com.backend.domain.chat.entity.MessageType;
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

}
