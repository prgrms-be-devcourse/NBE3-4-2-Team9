package com.backend.domain.voter.dto;

import com.backend.domain.voter.domain.VoterType;
import com.backend.global.validator.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * VoterCreateRequest
 * <p>추천 등록시 사용할 요청 객체 입니다.</p>
 *
 * @param targetId 타겟 ID
 * @param voterType 추천 타입 {@link VoterType}
 * @author Kim Dong O
 */
@Builder
public record VoterCreateRequest(
	@NotNull(message = "타겟 ID는 필수 입니다.")
	Long targetId,
	@ValidEnum(enumClass = VoterType.class)
	VoterType voterType) {

}
