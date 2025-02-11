package com.backend.domain.recruitmentUser.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthorRequest(
        @NotNull(message = "모집 인원 Id는 필수입니다.")
        Long userId
) {

}
