package com.ecommerce.controllers;

import com.ecommerce.dto.response.ProductImageResponse;
import com.ecommerce.services.impl.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ecommerce.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<ProductImageResponse> uploadImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productImageService.uploadImage(productId, file, userDetails.getId()));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImagesByProduct(productId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @PathVariable Long imageId) {
        productImageService.deleteImage(productId, imageId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<Void> setPrimary(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @PathVariable Long imageId) {
        productImageService.setPrimary(productId, imageId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderImages(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId, @RequestBody List<Long> imageIds) {
        productImageService.reorderImages(productId, imageIds, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
