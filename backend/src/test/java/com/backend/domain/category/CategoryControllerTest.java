package com.backend.domain.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.dto.request.CategoryRequest;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.service.CategoryService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
// 컨트롤러, 서비스, 리포 등 모든 빈을 가져옴
// 서비스 단만 테스트를 할땐 단위 테스트
// 단위 테스트는 하나의 클래스만 검증 서비스 안의 리포지토리 동작은 다 Mock처리로 진행
// 단위 테스트, 통합 테스트 차이는 속도
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
    @Autowired
    private CategoryService categoryService;

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
    private CategoryRequest createCategoryRequest (String name){
        return new CategoryRequest(name);  // 엔티티 생성 방식에서 request DTO 방식으로 변경
    }


    @Test
    @DisplayName("카테고리 조회")
    @Order(1)
    void getAllCategory_ShouldReturnCategoryList() throws Exception {
        // given: 미리 정의된 categoryResponse 리스트 반환 설정

        // when & then: GET 요청 보내고, JSON 응답의 특정 필드 값 검증
        mockMvc.perform(get("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())  // HTTP 200 OK 상태 코드 확인
                .andExpect(jsonPath("$.success").value(true))  // success 필드 값이 true 인지 확인
                .andExpect(jsonPath("$.data.length()").value(2));  // 데이터 2개인지 확인
    }

    @Test
    @DisplayName("카테고리 추가 - 관리자")
    void createCategory_WithAdminRole() throws Exception {

        // when & then: POST 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 201(Created) 확인
        CategoryRequest categoryRequest = createCategoryRequest("Tech");

        mockMvc.perform(post("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())  // 응답 상태 코드가 201인지 확인
                .andExpect(jsonPath("$.success").value(true))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.data.name").value("Tech"));  // 생성된 카테고리의 name이 "Tech"인지 확인


        // 수정 후 카테고리 목록 조회하여 수정 되어있는지 확인
        mockMvc.perform(get("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()) // 응답 상태 코드가 200인지
                .andExpect(jsonPath("$.data").isArray()) // 데이터가 배열인지 확인
                .andExpect(jsonPath("$.data.length()").value(3)) // 데이터 개수가 1인지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 추가 - 유저")
    void createCategory_WithUserRole() throws Exception {

        // when & then: POST 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 403(Created) 확인
        CategoryRequest categoryRequest = createCategoryRequest("Tech");

        mockMvc.perform(post("/api/v1/category")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden())  // 응답 상태 코드가 403인지 확인
                .andExpect(jsonPath("$.success").value(false))  // 응답의 success가 false인지 확인
                .andExpect(jsonPath("$.code").value(403))
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 수정 - 관리자")
    void updateCategory_WithAdminRole() throws Exception {
        CategoryRequest categoryRequest = createCategoryRequest("new Tech");

        mockMvc.perform(patch("/api/v1/category/{id}", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())  // 응답 상태 코드가 200인지
                .andExpect(jsonPath("$.success").value(true))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.data.name").value("new Tech"))
                .andDo(print());

        // 수정 후 카테고리 목록 조회하여 수정 되어있는지 확인
        mockMvc.perform(get("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()) // 응답 상태 코드가 200인지
                .andExpect(jsonPath("$.data").isArray()) // 데이터가 배열인지 확인
                .andExpect(jsonPath("$.data.length()").value(2)) // 데이터 개수가 1인지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 수정 - 유저")
    void updateCategory_WithUserRole() throws Exception {
        // 테스트용 categoryRequest
        CategoryRequest categoryRequest = createCategoryRequest("new Tech");

        mockMvc.perform(patch("/api/v1/category/{id}", 1L)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden())  // 응답 상태 코드가 403인지
                .andExpect(jsonPath("$.success").value(false))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.code").value(403))
                .andDo(print());
    }


    @Test
    @DisplayName("카테고리 삭제 - 관리자")
    void deleteCategory_WithAdminRole() throws Exception {
        // 테스트용 categoryRequest
        CategoryRequest categoryRequest = createCategoryRequest("Tech");

        mockMvc.perform(delete("/api/v1/category/{id}", 1L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent()) // 응답 상태 코드가 204인지
                .andDo(print());

        // 삭제 후 카테고리 목록 조회하여 비어있는지 확인
        mockMvc.perform(get("/api/v1/category")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()) // 응답 상태 코드가 200인지
                .andExpect(jsonPath("$.data").isArray()) // 데이터가 배열인지 확인
                .andExpect(jsonPath("$.data.length()").value(1)) // 데이터 개수가 1인지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 삭제 - 유저")
    void deleteCategory_WithUserRole() throws Exception {
        // 테스트용 categoryRequest
        CategoryRequest categoryRequest = createCategoryRequest("Tech");

        mockMvc.perform(delete("/api/v1/category/{id}", 1L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden()) // 응답 상태 코드가 403인지
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }
}