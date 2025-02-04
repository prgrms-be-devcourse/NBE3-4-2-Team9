package com.backend.domain.chat.dto.request;

import com.backend.domain.chat.entity.MessageType;

public record ChatRequest(
		MessageType type,
		Long userId,
		String content
) {

}