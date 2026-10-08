package com.ecommerce.services.impl;
import com.ecommerce.dto.request.CategoryRequest;
import com.ecommerce.dto.response.CategoryResponse;
import com.ecommerce.models.Category;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final com.ecommerce.repositories.UserRepository userRepository;
    @Override
    public CategoryResponse addCategory(CategoryRequest request, Long sellerId) {
        com.ecommerce.models.User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        Category category = categoryMapper.toEntity(request);
        category.setSeller(seller);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request, Long sellerId) {
        Category category = categoryRepository.findById(id).orElseThrow();
        if (category.getSeller() != null && !category.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this category");
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
    @Override
    public void deleteCategory(Long id, Long sellerId) {
        Category category = categoryRepository.findById(id).orElseThrow();
        if (category.getSeller() != null && !category.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this category");
        }
        categoryRepository.deleteById(id);
    }
    @Override
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toResponse(categoryRepository.findById(id).orElseThrow());
    }
    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }
    @Override
    public List<CategoryResponse> getCategoriesBySeller(Long sellerId) {
        return categoryMapper.toResponseList(categoryRepository.findBySellerId(sellerId));
    }
}
