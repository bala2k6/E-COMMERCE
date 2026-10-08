package com.ecommerce.services;
import com.ecommerce.dto.request.CategoryRequest;
import com.ecommerce.dto.response.CategoryResponse;
import java.util.List;
public interface CategoryService {
    CategoryResponse addCategory(CategoryRequest request, Long sellerId);
    CategoryResponse updateCategory(Long id, CategoryRequest request, Long sellerId);
    void deleteCategory(Long id, Long sellerId);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getCategoriesBySeller(Long sellerId);
}
