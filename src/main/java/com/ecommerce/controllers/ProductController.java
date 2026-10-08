package com.ecommerce.controllers;

import com.ecommerce.dto.request.PricingRequest;
import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.response.ProductVariantResponse;
import com.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ecommerce.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final com.ecommerce.services.impl.ProductImageService productImageService;
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllActiveProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }
    @GetMapping("/deals")
    public ResponseEntity<List<ProductResponse>> getDeals() {
        return ResponseEntity.ok(productService.getDeals());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @PostMapping(consumes = { org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("product") @Valid ProductRequest product,
            @RequestPart(value = "images", required = false) List<org.springframework.web.multipart.MultipartFile> images) {
        
        ProductResponse createdProduct = productService.createProduct(product, userDetails.getId());
        
        if (images != null && !images.isEmpty()) {
            for (org.springframework.web.multipart.MultipartFile img : images) {
                productImageService.uploadImage(createdProduct.getId(), img, userDetails.getId());
            }
            return ResponseEntity.ok(productService.getProductById(createdProduct.getId()));
        }
        
        return ResponseEntity.ok(createdProduct);
    }
    
    @PostMapping("/{id}/main-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductResponse> uploadMainImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(productService.uploadMainImage(id, file, userDetails.getId()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @Valid @RequestBody ProductRequest product) {
        return ResponseEntity.ok(productService.updateProduct(id, product, userDetails.getId()));
    }
    @PutMapping("/{id}/variants/{variantId}/pricing")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductVariantResponse> updateVariantPricing(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @PathVariable Long variantId, @RequestBody PricingRequest request) {
        return ResponseEntity.ok(productService.updateVariantPricing(id, variantId, request, userDetails.getId()));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        productService.deleteProduct(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }
    

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@AuthenticationPrincipal UserDetailsImpl userDetails, Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable, userDetails.getId()));
    }
}
