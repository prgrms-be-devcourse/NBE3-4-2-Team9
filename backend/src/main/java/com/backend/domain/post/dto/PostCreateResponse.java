package com.backend.domain.post.dto;

import lombok.Builder;

@Builder
public record PostCreateResponse(Long postId, Long categoryId) {

}
