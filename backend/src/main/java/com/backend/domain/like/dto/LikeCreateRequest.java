package com.backend.domain.like.dto;

import com.backend.domain.like.domain.LikeType;
import com.backend.global.validator.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * LikeCreateRequest
 * <p>관심 등록시 사용할 요청 객체 입니다.</p>
 *
 * @param targetId 타겟 ID
 * @param likeType 관심 타입 {@link LikeType}
 * @author Kim Dong O
 */
@Builder
public record LikeCreateRequest(
	@NotNull(message = "타겟 ID는 필수 입니다.")
	Long targetId,
	@ValidEnum(enumClass = LikeType.class)
	LikeType likeType) {

}
