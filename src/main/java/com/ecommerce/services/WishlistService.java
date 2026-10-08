package com.ecommerce.services;
import java.util.List;

public interface WishlistService {
    List<Long> getWishlistProductIds(Long userId);
    void addProductToWishlist(Long userId, Long productId);
    void removeProductFromWishlist(Long userId, Long productId);
    void clearWishlist(Long userId);
}
