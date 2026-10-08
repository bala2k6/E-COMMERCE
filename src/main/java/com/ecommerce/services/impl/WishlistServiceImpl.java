package com.ecommerce.services.impl;

import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.models.WishlistItem;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.repositories.WishlistItemRepository;
import com.ecommerce.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Long> getWishlistProductIds(Long userId) {
        return wishlistItemRepository.findByUserId(userId).stream()
                .map(item -> item.getProduct().getId())
                .toList();
    }

    @Override
    @Transactional
    public void addProductToWishlist(Long userId, Long productId) {
        if (wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) {
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();
        wishlistItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeProductFromWishlist(Long userId, Long productId) {
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        wishlistItemRepository.deleteByUserId(userId);
    }
}
