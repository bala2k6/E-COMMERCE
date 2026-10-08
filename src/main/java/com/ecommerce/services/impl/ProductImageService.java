package com.ecommerce.services.impl;

import com.ecommerce.dto.response.ProductImageResponse;
import com.ecommerce.mapper.ProductImageMapper;
import com.ecommerce.models.Product;
import com.ecommerce.models.ProductImage;
import com.ecommerce.repositories.ProductImageRepository;
import com.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    
    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    public ProductImageResponse uploadImage(Long productId, MultipartFile file, Long sellerId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getSeller() != null && !product.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
            
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }
            
        try {
            ProductImage productImage = ProductImage.builder()
                .product(product)
                .url("")
                .sortOrder(product.getImages() != null ? product.getImages().size() : 0)
                .isPrimary(product.getImages() == null || product.getImages().isEmpty())
                .imageData(file.getBytes())
                .contentType(contentType)
                .build();
                
            productImage = productImageRepository.save(productImage);
            productImage.setUrl("/api/images/" + productImage.getId());
            productImage = productImageRepository.save(productImage);
                
            if (productImage.isPrimary()) {
                product.setImageUrl(productImage.getUrl());
                productRepository.save(product);
            }
                
            return productImageMapper.toResponse(productImage);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
    
    public List<ProductImageResponse> getImagesByProduct(Long productId) {
        return productImageMapper.toResponseList(productImageRepository.findByProductId(productId));
    }

    public void deleteImage(Long productId, Long imageId, Long sellerId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getSeller() != null && !product.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found"));
            
        // Physical file deletion logic removed, DB deletion is sufficient
        
        productImageRepository.delete(image);
    }
    
    public void setPrimary(Long productId, Long imageId, Long sellerId) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getSeller() != null && !product.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        for (ProductImage img : product.getImages()) {
            if (img.getId().equals(imageId)) {
                img.setPrimary(true);
                product.setImageUrl(img.getUrl());
            } else {
                img.setPrimary(false);
            }
        }
        productRepository.save(product);
    }
    
    public void reorderImages(Long productId, List<Long> imageIds, Long sellerId) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getSeller() != null && !product.getSeller().getId().equals(sellerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You don't have permission to modify this product");
        }
        for (int i = 0; i < imageIds.size(); i++) {
            Long imgId = imageIds.get(i);
            product.getImages().stream()
                .filter(img -> img.getId().equals(imgId))
                .findFirst()
                .ifPresent(img -> img.setSortOrder(imageIds.indexOf(imgId)));
        }
        productRepository.save(product);
    }
}