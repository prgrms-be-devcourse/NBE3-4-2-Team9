package com.backend.global.security.filter;

import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.SecurityConfig;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.AuthResponseUtil;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        if (requestURI.equals("/api/v1/reissue")) {
            reissueFilter(req, resp);
        } else {
            accessFilter(req, resp, filterChain);
        }
    }

    private void reissueFilter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String refreshToken = getRefreshToken(req);

        if (refreshToken == null) {
            AuthResponseUtil.failLogin(
                    resp,
                    GenericResponse.of(false, GlobalErrorCode.BAD_REQUEST.getCode()),
                    HttpServletResponse.SC_BAD_REQUEST,
                    objectMapper);
            return;
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        if (!redisRepository.get(username).equals(refreshToken)) {
            AuthResponseUtil.failLogin(
                    resp,
                    GenericResponse.of(false, GlobalErrorCode.BAD_REQUEST.getCode()),
                    HttpServletResponse.SC_BAD_REQUEST,
                    objectMapper
            );
            return;
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                SiteUser.builder()
                        .email(username)
                        .userRole(role)
                        .id(jwtUtil.getUserId(refreshToken))
                        .build()
        );

        String newAccessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION);
        String newRefreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);

        redisRepository.remove(userDetails.getUsername());
        redisRepository.save(userDetails.getUsername(), newRefreshToken, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        AuthResponseUtil.success(
                resp,
                newAccessToken,
                jwtUtil.setJwtCookie("refreshToken", newRefreshToken, REFRESH_EXPIRATION),
                HttpServletResponse.SC_OK,
                GenericResponse.of(true, 200, userDetails.getUsername(), "AccessToken 재발급 성공"),
                objectMapper);
    }

    private void accessFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String authorization = req.getHeader("Authorization");

        if (isPublicUrl(req)) {
            filterChain.doFilter(req, resp);
            return;
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // Public URL이면 통과
            if (isPublicUrl(req)) {
                filterChain.doFilter(req, resp);
                return ;
            }

            AuthResponseUtil.failLogin(
                    resp,
                    GenericResponse.of(false, GlobalErrorCode.UNAUTHENTICATION_USER.getCode()),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    objectMapper);
            return;
        }

        String accessToken = authorization.substring(7);

        try {
            jwtUtil.isExpired(accessToken);
            String username = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);
            Long userId = jwtUtil.getUserId(accessToken);

            CustomUserDetails userDetails = new CustomUserDetails(
                    SiteUser.builder()
                            .id(userId)
                            .email(username)
                            .userRole(role)
                            .build()
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(req, resp);
        } catch (ExpiredJwtException e) {
            AuthResponseUtil.failLogin(
                    resp,
                    GenericResponse.of(false, GlobalErrorCode.UNAUTHENTICATION_USER.getCode()),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    objectMapper);
        } catch (JwtException e) {
            AuthResponseUtil.failLogin(
                    resp,
                    GenericResponse.of(false, GlobalErrorCode.INVALID_TOKEN.getCode()),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    objectMapper);
        }

    }

    private String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        String refreshToken = null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        return refreshToken;
    }

    private boolean isPublicUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        List<String> patterns = SecurityConfig.getPublicUrls().get(method);
        if (patterns == null) return false;

        return patterns.stream()
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, requestUri));
    }

}
