package com.backend.domain.category.service;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회
    public List<CategoryResponse> categoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return mappingCategoryList(categoryList);
    }

    // 카테고리 추가 (관리자만 등록 가능)
    public CategoryResponse createCategory(Category category) {
        try {
            // 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // UserRole 역할 정보 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Enum으로 반환
            UserRole userRole = UserRole.fromString(userDetails.getRole());

            // 관리자 권한 체크
            if (!userRole.isAdmin()) {
                throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
            }

            // 관리자일 경우 카테고리 등록 로직 실행
            Category saveCategory = categoryRepository.save(category);
            return mappingCategory(saveCategory);

        } catch (DataAccessException e) {
            // 데이터베이스 예외 처리
            throw new GlobalException(GlobalErrorCode.DATABASE_ACCESS_ERROR);

        } catch (Exception e) {
            // 기타 예외 처리 (서버 오류로 예외 처리)
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 카테고리 매핑
    private List<CategoryResponse> mappingCategoryList(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt().toLocalDateTime())
                        .modifiedAt(category.getModifiedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리 매핑 (단일 객체)
    private CategoryResponse mappingCategory(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt().toLocalDateTime())
                .modifiedAt(category.getModifiedAt().toLocalDateTime())
                .build();
    }
}