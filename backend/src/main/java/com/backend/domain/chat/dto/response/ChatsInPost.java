package com.backend.domain.chat.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record ChatsInPost(
        List<ChatResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    public ChatsInPost(Page<ChatResponse> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
