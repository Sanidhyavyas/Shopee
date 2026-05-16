package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.CategoryDto;
import com.shopee.shopee_backend.dto.CreateCategoryRequestDto;
import com.shopee.shopee_backend.entity.Category;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.CategoryRepository;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FranchiseRepository franchiseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByFranchise(Long franchiseId) {
        return categoryRepository.findAllByFranchiseFranchiseId(franchiseId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto createCategory(Long franchiseId, CreateCategoryRequestDto request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        if (categoryRepository.existsByNameAndFranchiseFranchiseId(request.getName(), franchiseId)) {
            throw new ApiException("Category '" + request.getName() + "' already exists in this franchise",
                    HttpStatus.CONFLICT);
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setFranchise(franchise);

        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long franchiseId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        if (!category.getFranchise().getFranchiseId().equals(franchiseId)) {
            throw new ApiException("Category does not belong to franchise " + franchiseId, HttpStatus.FORBIDDEN);
        }
        categoryRepository.delete(category);
    }

    private CategoryDto toDto(Category c) {
        return new CategoryDto(c.getCategoryId(), c.getName(), c.getDescription(),
                c.getFranchise().getFranchiseId());
    }
}
