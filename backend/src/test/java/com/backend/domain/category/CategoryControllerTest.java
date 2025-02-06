package com.backend.domain.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.dto.request.CategoryRequest;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestInstance(Lifecycle.PER_CLASS)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    // Jwt 관련 빈들은 SecurtiyConfig에 의해 사용, 여기선 Mock 객체로 주입
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    private long accessExpiration;

    private CategoryResponse categoryResponse;
    private String adminToken;
    private String userToken;

    @BeforeAll
    void setUp() throws Exception {
        SiteUser adminUser = userRepository.findByEmail("admin@naver.com").get();
        SiteUser user = userRepository.findByEmail("testEmail1@naver.com").get();

        CustomUserDetails adminCustomUserDetails = new CustomUserDetails(adminUser);
        CustomUserDetails userCustomUserDetails = new CustomUserDetails(user);

        adminToken = jwtUtil.createAccessToken(adminCustomUserDetails, accessExpiration);
        userToken = jwtUtil.createRefreshToken(userCustomUserDetails, accessExpiration);
    }
        // CategoryRequest 생성 메서드 (CategoryRequest로 변경된 부분)
        private CategoryRequest createCategoryRequest (ZonedDateTime now, String name){
            return new CategoryRequest(name, now, now);  // 엔티티 생성 방식에서 request DTO 방식으로 변경
        }


    /**
     * GET /api/v1/category 요청 테스트
     * 인증된 사용자(@WithMockUser)로 GET 요청을 보내 카테고리 목록을 정상적으로 조회하는지 확인
     * - categoryService.categoryList()에서 반환된 응답이 올바른지 검증
     */
    @Test
    void getAllCategory_ShouldReturnCategoryList() throws Exception {
        // given: 미리 정의된 categoryResponse 리스트 반환 설정

        // when & then: GET 요청 보내고, JSON 응답의 특정 필드 값 검증
        mockMvc.perform(get("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())  // HTTP 200 OK 상태 코드 확인
                .andExpect(jsonPath("$.success").value(true))  // success 필드 값이 true 인지 확인
                .andExpect(jsonPath("$.data.length()").value(2));  // 데이터 2개인지 확인
    }

    /*
     * POST /api/v1/category 요청 테스트 (관리자 권한)
     * ADMIN 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하는 경우 정상적으로 생성되었는지 확인
     * - 응답 상태 코드 201, success: true, 생성된 카테고리의 name 필드가 "Tech"인지 확인

     */

    @Test
    void createCategory_WithAdminRole_ShouldReturnCreated() throws Exception {

        // when & then: POST 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 201(Created) 확인
        CategoryRequest categoryRequest = createCategoryRequest(ZonedDateTime.now(), "Tech");

        mockMvc.perform(post("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())  // 응답 상태 코드가 201인지 확인
                .andExpect(jsonPath("$.success").value(true))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.data.name").value("Tech"));  // 생성된 카테고리의 name이 "Tech"인지 확인
    }
/**
    *
     * POST /api/v1/category 요청 테스트 (일반 사용자 권한)
     * USER 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하려 할 때 권한 부족으로 인해 403 Forbidden 상태 코드가 반환되는지 검증

    @Test
    void createCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        // when & then: POST 요청 시, 응답 상태가 403(Forbidden)임을 확인
        CategoryRequest categoryRequest = createCategoryRequest(ZonedDateTime.now(), "Tech");

        mockMvc.perform(post("/api/v1/category")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden());  // 권한 부족으로 인해 403 Forbidden이 반환되어야 함
    }

    *
     * PATCH /api/v1/category/{id} 요청 테스트 (관리자 권한)

    @Test
    void updateCategory_WithAdminRole_ShouldReturnUpdated() throws Exception {
        // given
        CategoryRequest updatedCategoryRequest = new CategoryRequest("Updated Tech", ZonedDateTime.now(), ZonedDateTime.now());
        CategoryResponse updatedCategoryResponse = createCategoryResponse(ZonedDateTime.now(), "Updated Tech");

        // 토큰이 만료되지 않았는지 먼저 확인
        System.out.println("Admin Token: " + adminToken);  // 토큰 값 확인

        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
                .thenReturn(updatedCategoryResponse);

        // when & then
        MvcResult result = mockMvc.perform(patch("/api/v1/category/{id}", 1L)
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Tech"))
                .andDo(print())  // 실패 시 응답 내용 출력
                .andReturn();

        // 응답 내용 확인
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    *
     * PATCH /api/v1/category/{id} 요청 테스트 (일반 사용자 권한)

    @Test
    void updateCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        // when & then: PATCH 요청 시, 응답 상태가 403(Forbidden)임을 확인
        CategoryRequest updatedCategoryRequest = new CategoryRequest("Updated Tech", ZonedDateTime.now(), ZonedDateTime.now());

        mockMvc.perform(patch("/api/v1/category/{id}", 1L)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryRequest)))
                .andExpect(status().isForbidden());  // 권한 부족으로 인해 403 Forbidden이 반환되어야 함
    }

    @Test
    void updateCategory_WithInvalidName_ShouldReturnBadRequest() throws Exception {
        // given: 유효하지 않은 카테고리 이름 설정
        CategoryRequest invalidCategoryRequest = new CategoryRequest(null, ZonedDateTime.now(), ZonedDateTime.now());

        // when & then: PATCH 요청을 보낼 때, 유효성 검사에서 예외가 발생하고 상태 코드 400을 반환하는지 확인
        mockMvc.perform(patch("/api/v1/category/{id}", 1L)
                        .with(csrf())  // CSRF 보호 활성화
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategoryRequest)))
                .andExpect(status().isBadRequest())  // 상태 코드 400(Bad Request) 확인
                .andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."));  // 예외 메시지 확인
    }*/
}