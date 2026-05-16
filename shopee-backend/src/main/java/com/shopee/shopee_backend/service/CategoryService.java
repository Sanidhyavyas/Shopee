package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CategoryDto;
import com.shopee.shopee_backend.dto.CreateCategoryRequestDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategoriesByFranchise(Long franchiseId);

    CategoryDto createCategory(Long franchiseId, CreateCategoryRequestDto request);

    void deleteCategory(Long franchiseId, Long categoryId);
}
