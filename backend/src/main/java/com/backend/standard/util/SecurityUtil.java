package com.backend.standard.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.backend.global.security.custom.CustomUserDetails;

@Component
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID를 Optional로 반환합니다.
     * 인증되지 않은 사용자의 경우 빈 Optional을 반환합니다.
     *
     * @return Optional<Long> 현재 사용자의 ID (인증되지 않은 경우 빈 Optional 반환)
     */
    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 경우 빈 Optional 반환
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getId());
        }

        // 인증 정보에서 사용자 ID를 찾을 수 없는 경우 빈 Optional 반환
        return Optional.empty();
    }
}
