package com.ecommerce.services.impl;

import com.ecommerce.services.ProductService;
import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.dto.request.PricingRequest;
import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.ProductVariantResponse;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.ProductVariantMapper;
import com.ecommerce.models.Category;
import com.ecommerce.models.ProductVariant;
import com.ecommerce.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ecommerce.models.Product;
import com.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final com.ecommerce.repositories.UserRepository userRepository;

    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(productMapper::toResponse);
    }
    
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchActiveProducts(keyword, pageable).map(productMapper::toResponse);
    }
    
    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(productMapper::toResponse);
    }
    
    public ProductResponse getProductById(Long id) {
        Product p = productRepository.findByIdWithImages(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return productMapper.toResponse(p);
    }
    
    public ProductResponse createProduct(ProductRequest request, Long sellerId) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        com.ecommerce.models.User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSeller(seller);
        if (product.getVariants() != null) {
            product.getVariants().forEach(v -> v.setProduct(product));
        }
        return productMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (p.getSeller() != null && !p.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setBasePrice(request.getBasePrice());
        p.setImageUrl(request.getImageUrl());
        p.setCategory(category);
        
        if (request.getVariants() != null) {
            java.util.List<Long> requestedVariantIds = request.getVariants().stream()
                    .filter(v -> v.getId() != null)
                    .map(com.ecommerce.dto.request.ProductVariantRequest::getId)
                    .collect(java.util.stream.Collectors.toList());
            p.getVariants().removeIf(v -> v.getId() != null && !requestedVariantIds.contains(v.getId()));

            for (com.ecommerce.dto.request.ProductVariantRequest vr : request.getVariants()) {
                ProductVariant variant = null;
                if (vr.getId() != null) {
                    variant = p.getVariants().stream()
                            .filter(v -> v.getId().equals(vr.getId()))
                            .findFirst()
                            .orElse(null);
                }
                if (variant == null) {
                    variant = productVariantMapper.toEntity(vr);
                    variant.setProduct(p);
                    p.getVariants().add(variant);
                } else {
                    variant.setSku(vr.getSku());
                    variant.setSize(vr.getSize());
                    variant.setColor(vr.getColor());
                    variant.setVariantName(vr.getSize() + " / " + vr.getColor());
                    variant.setPrice(vr.getPrice());
                    variant.setMrp(vr.getMrp());
                    variant.setSellingPrice(vr.getSellingPrice());
                    variant.setDiscountType(vr.getDiscountType() != null ? ProductVariant.DiscountType.valueOf(vr.getDiscountType()) : null);
                    variant.setDiscountValue(vr.getDiscountValue());
                    variant.setDiscountStartAt(vr.getDiscountStartAt());
                    variant.setDiscountEndAt(vr.getDiscountEndAt());
                    variant.setStockQuantity(vr.getStockQuantity());
                }
            }
        }
        
        return productMapper.toResponse(productRepository.save(p));
    }
    
    public ProductResponse uploadMainImage(Long id, org.springframework.web.multipart.MultipartFile file, Long sellerId) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (p.getSeller() != null && !p.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        try {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            String originalFileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
            String fileName = java.util.UUID.randomUUID().toString() + "_" + originalFileName;
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            p.setImageUrl("/images/" + fileName);
            return productMapper.toResponse(productRepository.save(p));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    public ProductVariantResponse updateVariantPricing(Long productId, Long variantId, PricingRequest request, Long sellerId) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        if (p.getSeller() != null && !p.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        ProductVariant variant = p.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Variant", variantId));
                 
        if (request.getPrice() != null) {
            variant.setPrice(request.getPrice());
        }
        variant.setMrp(request.getMrp());
        variant.setSellingPrice(request.getSellingPrice());
        if (request.getDiscountType() != null) {
            variant.setDiscountType(ProductVariant.DiscountType.valueOf(request.getDiscountType()));
        }
        variant.setDiscountValue(request.getDiscountValue());
        variant.setDiscountStartAt(request.getDiscountStartAt());
        variant.setDiscountEndAt(request.getDiscountEndAt());
        
        // Compute selling price if not provided but discount is
        if (variant.getMrp() != null && variant.getDiscountType() != null && variant.getDiscountValue() != null) {
            if (variant.getDiscountType() == ProductVariant.DiscountType.PERCENTAGE) {
                java.math.BigDecimal discountAmount = variant.getMrp().multiply(variant.getDiscountValue()).divide(new java.math.BigDecimal("100"));
                variant.setSellingPrice(variant.getMrp().subtract(discountAmount));
            } else if (variant.getDiscountType() == ProductVariant.DiscountType.FLAT) {
                variant.setSellingPrice(variant.getMrp().subtract(variant.getDiscountValue()));
            }
        }
        
        productRepository.save(p);
        return productVariantMapper.toResponse(variant);
    }
    
    public java.util.List<ProductResponse> getDeals() {
        // Find products where at least one variant has an active discount
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.util.List<Product> products = productRepository.findAll().stream()
            .filter(Product::isActive)
            .filter(p -> p.getSeller() != null && p.getSeller().isActive())
            .filter(p -> p.getVariants().stream().anyMatch(v -> 
                v.getDiscountStartAt() != null && v.getDiscountEndAt() != null &&
                now.isAfter(v.getDiscountStartAt()) && now.isBefore(v.getDiscountEndAt())
            ))
            .toList();
        return productMapper.toResponseList(products);
    }
    
    public void deleteProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (p.getSeller() != null && !p.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        p.setActive(false); // soft delete
        productRepository.save(p);
    }
    
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, Long sellerId) {
        return productRepository.findBySellerIdAndActiveTrue(sellerId, pageable).map(productMapper::toResponse);
    }
}
