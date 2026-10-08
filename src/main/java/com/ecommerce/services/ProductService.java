package com.ecommerce.services;
import com.ecommerce.dto.request.PricingRequest;
import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.ProductVariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllActiveProducts(Pageable pageable);
    Page<ProductResponse> getAllProducts(Pageable pageable, Long sellerId);
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request, Long sellerId);
    ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId);
    ProductResponse uploadMainImage(Long id, org.springframework.web.multipart.MultipartFile file, Long sellerId);
    ProductVariantResponse updateVariantPricing(Long productId, Long variantId, PricingRequest request, Long sellerId);
    List<ProductResponse> getDeals();
    void deleteProduct(Long id, Long sellerId);
}
