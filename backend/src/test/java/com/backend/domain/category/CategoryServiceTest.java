package com.backend.global.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.category.service.CategoryService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceTest {

    // Repository를 Mock 객체로 주입
    @Mock
    private CategoryRepository categoryRepository;

    // Security 관련 Mock 객체들
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    // 테스트 대상 객체(CategoryService) 생성, 목 객체들이 주입됨
    @InjectMocks
    private CategoryService categoryService;

    // 테스트에서 사용할 Category, 사용자 정보, 현재 시간 변수
    private Category category;
    private CustomUserDetails adminDetails;
    private CustomUserDetails userDetails;
    private ZonedDateTime now;

    @BeforeEach
    void setUp() {

        // 테스트 실행 시점의 현재 시간을 저장
        now = ZonedDateTime.now();

        // Category 객체 생성 (id: 1, name: "Tech")
        // BaseEntity 상속으로 인해 createdAt, modifiedAt 필드가 있지만, 여기서는 Reflection을 통해 강제로 값을 주입
        category = new Category(1L, "Tech");
        ReflectionTestUtils.setField(category, "createdAt", now);
        ReflectionTestUtils.setField(category, "modifiedAt", now);

        // Admin 사용자 생성
        SiteUser adminUser = SiteUser.builder()
                .id(1L)
                .email("admin@admin.com")
                .password("admin")
                .name("admin")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();

        // 일반 사용자 생성
        SiteUser normalUser = SiteUser.builder()
                .id(2L)
                .email("user@user.com")
                .password("user")
                .name("user")
                .userRole(UserRole.ROLE_USER.toString())
                .build();

        // CustomUserDetails 객체로 변환
        adminDetails = new CustomUserDetails(adminUser);
        userDetails = new CustomUserDetails(normalUser);
    }

    /**
     * SecurityContext를 Mocking하여 설정하는 유틸리티 메서드
     * 각 테스트에서 사용자가 다를 경우, 이 메서드를 호출하여 보안 정보를 설정
     */
    private void setSecurityContext(CustomUserDetails userDetails) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * 테스트: categoryList_ShouldReturnAllCategories
     *
     * 목적:
     * - CategoryService의 categoryList() 메서드가 CategoryRepository에서 조회한 모든 카테고리를
     *   CategoryResponse 리스트로 변환하여 반환하는지 검증.
     */
    @Test
    void categoryList_ShouldReturnAllCategories() {

        // given: "Tech" 및 "Science" 카테고리 객체 생성 및 createdAt, modifiedAt 값 설정
        Category category2 = new Category(2L, "Science");
        ReflectionTestUtils.setField(category2, "createdAt", now);
        ReflectionTestUtils.setField(category2, "modifiedAt", now);

        // 두 개의 Category 객체를 포함하는 리스트를 Mock Repository에서 반환하도록 설정
        List<Category> categoryList = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // when: CategoryService의 categoryList() 메서드 호출
        List<CategoryResponse> result = categoryService.categoryList();

        // then: 반환된 리스트의 사이즈와 각 CategoryResponse 객체의 값 검증
        assertThat(result).hasSize(2);                                  // 두 개의 카테고리가 반환되었는지 확인
        assertThat(result.get(0).getName()).isEqualTo("Tech");        // 첫 번째 카테고리 이름 검증
        assertThat(result.get(1).getName()).isEqualTo("Science");     // 두 번째 카테고리 이름 검증
    }

    /**
     * 테스트: createCategory_WithAdminRole_ShouldCreateCategory
     *
     * 목적:
     * - 관리자 권한을 가진 사용자가 카테고리 생성 요청을 했을 때, CategoryService가 정상적으로
     *   카테고리를 저장하고 생성된 CategoryResponse를 반환하는지 검증.
     */
    @Test
    void createCategory_WithAdminRole_ShouldCreateCategory() {

        // given: 관리자 권한을 가진 사용자의 SecurityContext 설정
        setSecurityContext(adminDetails);

        // Repository의 save() 메서드 호출 시, 미리 설정한 category 객체 반환하도록 설정
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when: CategoryService의 createCategory() 호출
        CategoryResponse result = categoryService.createCategory(category);

        // then: 반환된 CategoryResponse의 값 검증 (id, name 등이 올바르게 설정되었는지)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Tech");
    }

    /**
     * 테스트: createCategory_WithUserRole_ShouldThrowException
     *
     * 목적:
     * - 일반 사용자(ROLE_USER)가 카테고리 생성 요청을 했을 때, 권한 부족으로 인해 GlobalException이 발생하는지 검증.
     */
    @Test
    void createCategory_WithUserRole_ShouldThrowException() {

        // given: 일반 사용자 권한을 가진 사용자의 SecurityContext 설정
        setSecurityContext(userDetails);

        // when & then: createCategory() 호출 시 GlobalException이 발생하는지 검증
        assertThatThrownBy(() -> categoryService.createCategory(category))
                .isInstanceOf(GlobalException.class);
    }
}