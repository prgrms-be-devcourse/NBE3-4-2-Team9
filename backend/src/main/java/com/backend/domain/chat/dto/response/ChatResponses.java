package com.backend.domain.chat.dto.response;

import java.util.List;

public record ChatResponses(
        List<ChatResponse> chats
) {
}
