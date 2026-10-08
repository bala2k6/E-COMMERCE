package com.ecommerce.controllers;

import com.ecommerce.security.UserDetailsImpl;
import com.ecommerce.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {
    
    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Long>> getWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlistProductIds(userDetails.getId()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addProductToWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        wishlistService.addProductToWishlist(userDetails.getId(), productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProductFromWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        wishlistService.removeProductFromWishlist(userDetails.getId(), productId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        wishlistService.clearWishlist(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
