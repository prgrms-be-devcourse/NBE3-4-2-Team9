package com.backend.domain.like.dto;

import com.backend.domain.like.domain.LikeType;
import lombok.Builder;

/**
 * LikeCreateResponse
 * <p>Like 생성시 응답 객체 입니다.</p></p>
 * @param targetId 관심 타겟 ID
 * @param likeType {@link LikeType} 관심 타입
 */
@Builder
public record LikeCreateResponse(Long targetId, LikeType likeType) {

}
