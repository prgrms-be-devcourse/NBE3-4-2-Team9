package com.backend.domain.category.service;

import static com.backend.domain.category.converter.CategoryConverter.mappingCategory;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategoryList;

import com.backend.domain.category.converter.CategoryConverter;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

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

    // 카테고리 수정
    public CategoryResponse updateCategory(Category category) {
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

            // 관리자일 경우 기존 카테고리 조회
            Category findCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

            // 변경된 카테고리 객체
            Category updateCategory = Category.builder()
                    .id(findCategory.getId())
                    .name(category.getName())
                    .build();

            // 변경된 카테고리 저장
            Category saveCategory = categoryRepository.save(updateCategory);

            // 응답 객체로 변환 후 반환
            return mappingCategory(saveCategory);

        } catch (DataAccessException e) {
            // 데이터베이스 예외 처리
            throw new GlobalException(GlobalErrorCode.DATABASE_ACCESS_ERROR);

        } catch (Exception e) {
            // 기타 예외 처리 (서버 오류로 예외 처리)
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}