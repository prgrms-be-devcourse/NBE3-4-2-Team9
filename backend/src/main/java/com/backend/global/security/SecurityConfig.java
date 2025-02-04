package com.backend.global.security;


import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.filter.JwtAuthenticationFilter;
import com.backend.global.security.filter.JwtAuthorizationFilter;
import com.backend.global.security.handler.JwtLogoutHandler;
import com.backend.global.security.handler.JwtLogoutSuccessHandler;
import com.backend.global.security.handler.OAuth2LoginFailureHandler;
import com.backend.global.security.handler.OAuth2LoginSuccessHandler;
import com.backend.global.security.oauth.CustomOAuth2UserService;
import com.backend.standard.util.AuthResponseUtil;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.token.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration configuration, CorsConfigurationSource corsConfigurationSource) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository, authenticationManager(configuration)
        );

        jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/adm/login");

        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
                jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository
        );

        http.headers(head -> head
                .frameOptions(option -> option.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/h2-console/**", "/api/v1/chat/**", "/ws/**").permitAll()
                        .requestMatchers("/login/oauth2/code/kakao", "/oauth2/authorization/kakao").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/category").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/login/oauth2/code/kakao", "/oauth2/authorization/kakao").permitAll()
//                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                {
                                    AuthResponseUtil.failLogin(
                                        response, GenericResponse.of(false, 401), HttpServletResponse.SC_UNAUTHORIZED, objectMapper);
                                }))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, authException) ->
                        {
                            AuthResponseUtil.failLogin(
                                    response, GenericResponse.of(false, 403), HttpServletResponse.SC_FORBIDDEN, objectMapper);
                        }))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .addLogoutHandler(new JwtLogoutHandler(jwtUtil, redisRepository))
                        .logoutSuccessHandler(new JwtLogoutSuccessHandler(objectMapper))
                )
                .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                    )
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/login/oauth2/code/kakao")
                );

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // CORS 설정
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        // CORS 설정을 소스에 등록
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

}
