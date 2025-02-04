package com.backend.domain.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.security.handler.OAuth2LoginFailureHandler;
import com.backend.global.security.handler.OAuth2LoginSuccessHandler;
import com.backend.global.security.oauth.CustomOAuth2UserService;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // CategoryController가 의존하는 서비스 빈을 Mock 객체로 주입
    @MockitoBean
    private CategoryService categoryService;

    // Jwt 관련 빈들은 SecurtiyConfig에 의해 사용, 여기선 Mock 객체로 주입
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private RedisRepository redisRepository;
    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockitoBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @MockitoBean
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        // 테스트용 Category 객체 및 CategoryResponse 객체 초기화
        ZonedDateTime now = ZonedDateTime.now();
        category = createCategory(now, "Tech");
        categoryResponse = createCategoryResponse(now, "Tech");
    }

        // 카테고리 생성 메서드
        private Category createCategory(ZonedDateTime now, String name) {
            Category category = new Category(null, name);
            ReflectionTestUtils.setField(category, "createdAt", now);
            ReflectionTestUtils.setField(category, "modifiedAt", now);
            return category;
        }

        // CategoryResponse 생성 메서드
        private CategoryResponse createCategoryResponse(ZonedDateTime now, String name) {
            return CategoryResponse.builder()
                    .id(1L)
                    .name(name)
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();
        }

    /**
     * GET /api/v1/category 요청 테스트
     * 인증된 사용자(@WithMockUser)로 GET 요청을 보내 카테고리 목록을 정상적으로 조회하는지 확인
     * - categoryService.categoryList()에서 반환된 응답이 올바른지 검증
     */
    @Test
    @WithMockUser
    void getAllCategory_ShouldReturnCategoryList() throws Exception {
        // given: 미리 정의된 categoryResponse 리스트 반환 설정
        when(categoryService.categoryList()).thenReturn(Arrays.asList(categoryResponse));

        // when & then: GET 요청 보내고, JSON 응답의 특정 필드 값 검증
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())  // HTTP 200 OK 상태 코드 확인
                .andExpect(jsonPath("$.success").value(true))  // success 필드 값이 true 인지 확인
                .andExpect(jsonPath("$.data[0].name").value("Tech"));  // 첫 번째 데이터의 name이 "Tech" 인지 확인
    }

    /**
     * POST /api/v1/category 요청 테스트 (관리자 권한)
     * ADMIN 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하는 경우 정상적으로 생성되었는지 확인
     * - 응답 상태 코드 201, success: true, 생성된 카테고리의 name 필드가 "Tech"인지 확인
     */
    @Test
    @WithMockUser(roles = "ADMIN")  // ADMIN 권한으로 인증된 사용자로 요청
    void createCategory_WithAdminRole_ShouldReturnCreated() throws Exception {
        // given: categoryService.createCategory()에서 반환될 categoryResponse 설정
        when(categoryService.createCategory(any(Category.class))).thenReturn(categoryResponse);

        // when & then: POST 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 201(Created) 확인
        mockMvc.perform(post("/api/v1/category")
                        .with(csrf())  // CSRF 보호 활성화
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())  // 응답 상태 코드가 201인지 확인
                .andExpect(jsonPath("$.success").value(true))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.data.name").value("Tech"));  // 생성된 카테고리의 name이 "Tech"인지 확인
    }

    /**
     * POST /api/v1/category 요청 테스트 (일반 사용자 권한)
     * USER 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하려 할 때 권한 부족으로 인해 403 Forbidden 상태 코드가 반환되는지 검증
     */
    @Test
    @WithMockUser(roles = "USER")  // USER 권한으로 인증된 사용자로 요청
    void createCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        // when & then: POST 요청 시, 응답 상태가 403(Forbidden)임을 확인
        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isForbidden());  // 권한 부족으로 인해 403 Forbidden이 반환되어야 함
    }
}