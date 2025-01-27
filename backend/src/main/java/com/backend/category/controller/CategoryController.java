package com.backend.category.controller;

import com.backend.category.dto.CategoryDto;
import com.backend.category.service.CategoryService;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 전체 조회
    @GetMapping
    public GenericResponse<List<CategoryDto>> getAllCategory() {
        List<CategoryDto> categorieList = categoryService.categoryList();
        return GenericResponse.of(true, "200", categorieList);
    }
}
